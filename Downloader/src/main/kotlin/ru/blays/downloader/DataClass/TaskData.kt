package ru.blays.downloader.DataClass

sealed class DownloadMode {
    data object SingleTry : DownloadMode()

    class MultipleTry(tryCount: Int): DownloadMode()

    data object InfinityTry : DownloadMode()
}

enum class FileMode {
    ContinueIfExists,
    Recreate;
}