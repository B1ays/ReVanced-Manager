package ru.blays.revanced.data.Downloader

import ru.blays.revanced.data.Downloader.DataClass.DownloadInfo

interface Downloader {
    fun download(task: DownloadTask): DownloadInfo?
}