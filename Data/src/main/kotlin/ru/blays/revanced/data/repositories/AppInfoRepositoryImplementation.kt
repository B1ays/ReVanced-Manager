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

    private fun List<VersionsInfoModel>.mapVersionsInfoModelToDomainClass() : List<VersionsInfoModelDto> {

        val mappedList = mutableListOf<VersionsInfoModelDto>()

        this.forEach { item ->
            mappedList.add(
                VersionsInfoModelDto(
                    item.version,
                    item.patchesVersion,
                    item.changelogLink,
                    item.downloadLink
                )
            )
        }
        return mappedList
    }

    private fun List<ApkInfoModel>.mapApkInfoModelToDomainClass() : List<ApkInfoModelDto> {

        val mappedList = mutableListOf<ApkInfoModelDto>()

        this.forEach { item ->
            mappedList.add(
                ApkInfoModelDto(
                    item.type,
                    item.name,
                    item.description,
                    item.url
                )
            )
        }
        return mappedList
    }

    override suspend fun getVersionsInfo(jsonUrl: String): List<VersionsInfoModelDto>? = getHtmlBody(jsonUrl)
        ?.serializeJsonFromString<List<VersionsInfoModel>>()
        ?.mapVersionsInfoModelToDomainClass()


    override suspend fun getApkList(jsonUrl: String) : List<ApkInfoModelDto>? = getHtmlBody(jsonUrl)
        ?.serializeJsonFromString<List<ApkInfoModel>>()
        ?.mapApkInfoModelToDomainClass()

}