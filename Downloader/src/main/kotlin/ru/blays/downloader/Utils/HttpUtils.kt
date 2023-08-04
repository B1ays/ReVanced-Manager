package ru.blays.downloader.Utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

internal fun OkHttpClient.createResponse(request: Request) : Response {
    return  newCall(request).execute()
}