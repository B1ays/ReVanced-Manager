package ru.blays.revanced.Elements.DataClasses

import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.java.KoinJavaComponent.inject
import ru.blays.revanced.Elements.Elements.VectorImages.AppsIcons
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.Microg
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.MusicMonochrome
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.YoutubeMonochrome
import ru.blays.revanced.Elements.Repository.MicroGVersionsRepository
import ru.blays.revanced.Elements.Repository.VersionsRepository
import ru.blays.revanced.Elements.Repository.YoutubeMusicVersionsRepository
import ru.blays.revanced.Elements.Repository.YoutubeVersionsRepository

data class AppInfo(
    val appName: String? = null,
    val version: String? = null,
    val patchesVersion: String? = null,
    val packageName: String? = null
)

enum class Apps {

    YOUTUBE {
        override val icon = AppsIcons.YoutubeMonochrome
        override val repository: YoutubeVersionsRepository by inject(YoutubeVersionsRepository::class.java)
    },

    YOUTUBE_MUSIC {
        override val icon = AppsIcons.MusicMonochrome
        override val repository: YoutubeMusicVersionsRepository by inject(YoutubeMusicVersionsRepository::class.java)
    },

    MICROG {
        override val icon = AppsIcons.Microg
        override val repository: MicroGVersionsRepository by inject(
            MicroGVersionsRepository::class.java)
    };

    abstract val icon: ImageVector
    abstract val repository: VersionsRepository

}