package ru.blays.revanced.data.Downloader.Utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

fun OkHttpClient.createResponse(request: Request) : Response {
    return  newCall(request).execute()
}