package ru.blays.downloader

import ru.blays.downloader.DataClass.DownloadInfo

interface Downloader {
    fun download(task: DownloadTask): DownloadInfo?
}