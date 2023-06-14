package ru.blays.revanced.data.Downloader.DataClass

sealed class DownloadMode {
    class SingleTry: DownloadMode()

    class MultipleTry(tryCount: Int): DownloadMode()

    class InfinityTry: DownloadMode()
}

enum class FileMode {
    ContinueIfExists,
    Recreate;
}