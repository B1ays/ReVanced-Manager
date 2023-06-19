package ru.blays.revanced.data.repositories

import android.util.Log
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import ru.blays.revanced.data.DataModels.ApkInfoModel
import ru.blays.revanced.data.DataModels.VersionsInfoModel
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface
import java.util.concurrent.TimeUnit

class AppInfoRepositoryImplementation : AppInfoRepositoryInterface {

    private suspend fun getHtmlBody(url: String): String? {
        val client = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        if (url.isEmpty()) return null

        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException(
                        "Response not successful, errorCode:" +
                        " ${response.code} ${response.message}"
                    )
                }
                response.body.string()
            }
        } catch (e: IOException) {
            Log.w("VersionsInfo:","Response error, exception: $e")
            null
        }
    }

    private inline fun <reified T> String.serializeJsonFromString() : T = Json.decodeFromString(string = this)

    private fun List<VersionsInfoModel>.mapVersionsInfoModelToDomainClass() : List<VersionsInfoModelDto> = mutableListOf<VersionsInfoModelDto>().apply {
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

    private fun List<ApkInfoModel>.mapApkInfoModelToDomainClass() : List<ApkInfoModelDto> = mutableListOf<ApkInfoModelDto>().apply {
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

    override suspend fun getVersionsInfo(jsonUrl: String): List<VersionsInfoModelDto>? = getHtmlBody(jsonUrl)
        ?.serializeJsonFromString<List<VersionsInfoModel>>()
        ?.mapVersionsInfoModelToDomainClass()


    override suspend fun getApkList(jsonUrl: String) : List<ApkInfoModelDto>? = getHtmlBody(jsonUrl)
        ?.serializeJsonFromString<List<ApkInfoModel>>()
        ?.mapApkInfoModelToDomainClass()

    override suspend fun getChangelog(changelogUrl: String): String = getHtmlBody(changelogUrl).orEmpty()

}