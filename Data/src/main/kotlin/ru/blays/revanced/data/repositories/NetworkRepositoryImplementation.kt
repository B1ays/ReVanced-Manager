package ru.blays.revanced.data.repositories

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import ru.blays.revanced.data.CacheManager.CacheManagerInterface
import ru.blays.revanced.data.DataModels.ApkInfoModel
import ru.blays.revanced.data.DataModels.AppUpdateModel
import ru.blays.revanced.data.DataModels.VersionsInfoModel
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.AppUpdateModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.Repositories.NetworkRepositoryInterface
import ru.blays.revanced.shared.LogManager.BLog
import java.util.concurrent.TimeUnit

private const val TAG = "AppRepository"

class NetworkRepositoryImplementation(private val cacheManager: CacheManagerInterface, private val cacheLifetimeLong: Long): NetworkRepositoryInterface {

    private suspend fun router(url: String, recreateCache: Boolean): String? = coroutineScope {
        BLog.i(TAG, "data request. Url: $url, recreateCache: $recreateCache")
        var json: String?
        if (recreateCache) {
            json = getHtmlBody(url)
            launch {
                json?.let { cacheManager.addToCache(url, it) }
            }
            return@coroutineScope json
        } else {
            json = cacheManager.getJsonFromCache(url, cacheLifecycleLong = cacheLifetimeLong)
            if (json == null) {
                json = getHtmlBody(url)
            } else {
                return@coroutineScope json
            }
            launch {
                json?.let { cacheManager.addToCache(url, it) }
            }
            return@coroutineScope json
        }
    }

    private suspend fun getHtmlBody(url: String): String? = coroutineScope {
        val client = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        if (url.isEmpty()) return@coroutineScope null

        val request = Request.Builder()
            .url(url)
            .build()

        return@coroutineScope try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException()
                }
                response.body.string()
            }
        } catch (e: IOException) {
            BLog.w(TAG, "Response not successful, errorCode: ${e.message}")
            null
        }
    }

    private suspend inline fun <reified T> String.serializeJsonFromString(): T =
        coroutineScope { Json.decodeFromString(string = this@serializeJsonFromString) }

    private suspend fun List<VersionsInfoModel>.mapVersionsInfoModelToDomainClass(): List<VersionsInfoModelDto> =
        coroutineScope {
            mutableListOf<VersionsInfoModelDto>().apply {
                this@mapVersionsInfoModelToDomainClass.forEach { item ->
                    this.add(
                        VersionsInfoModelDto(
                            item.version,
                            item.patchesVersion,
                            item.buildDate,
                            item.changelogLink,
                            item.versionsListLink
                        )
                    )
                }
            }
        }

    private suspend fun List<ApkInfoModel>.mapApkInfoModelToDomainClass(): List<ApkInfoModelDto> =
        coroutineScope {
            mutableListOf<ApkInfoModelDto>().apply {
                this@mapApkInfoModelToDomainClass.forEach { item ->
                    this.add(
                        ApkInfoModelDto(
                            item.isRootVersion,
                            item.name,
                            item.description,
                            item.url,
                            item.originalApkLink
                        )
                    )
                }
            }
        }

    private suspend fun AppUpdateModel.mapAppUpdateModelToDomainClass(): AppUpdateModelDto = coroutineScope {
        AppUpdateModelDto(
            availableVersion,
            versionCode,
            buildDate,
            changelogLink,
            apkLink
        )
    }

    override suspend fun getVersionsInfo(
        jsonUrl: String,
        recreateCache: Boolean
    ): List<VersionsInfoModelDto>? = router(jsonUrl, recreateCache)
        ?.serializeJsonFromString<List<VersionsInfoModel>>()
        ?.mapVersionsInfoModelToDomainClass()


    override suspend fun getApkList(
        jsonUrl: String,
        recreateCache: Boolean
    ): List<ApkInfoModelDto>? = router(jsonUrl, recreateCache)
        ?.serializeJsonFromString<List<ApkInfoModel>>()
        ?.mapApkInfoModelToDomainClass()

    override suspend fun getManagerUpdateInfo(url: String, recreateCache: Boolean): AppUpdateModelDto? = router(url, recreateCache)
        ?.serializeJsonFromString<AppUpdateModel>()
        ?.mapAppUpdateModelToDomainClass()

    override suspend fun getText(changelogUrl: String, recreateCache: Boolean): String =
        router(changelogUrl, recreateCache).orEmpty()

}
