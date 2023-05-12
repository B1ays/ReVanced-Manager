package ru.blays.revanced.data.repositories

import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import ru.blays.revanced.data.DataClasses.ApkInfoModel
import ru.blays.revanced.data.DataClasses.VersionsInfoModel
import ru.blays.revanced.domain.DataClasses.ApkInfoModelDto
import ru.blays.revanced.domain.DataClasses.VersionsInfoModelDto
import ru.blays.revanced.domain.Repositories.AppInfoRepositoryInterface

class AppInfoRepositoryImplementation : AppInfoRepositoryInterface {

    val youTubeVersionsJsonLink = ""
    val musicVersionsJsonLink = ""

    private suspend fun getHtmlBody(url: String): String? {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException(
                        "Запрос к серверу не был успешен:" +
                        " ${response.code} ${response.message}"
                    )
                }
                response.body.string()
            }
        } catch (e: IOException) {
            Log.w("VersionsInfo:","Ошибка подключения: $e")
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
                    item.type,
                    item.name,
                    item.description,
                    item.url
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

}