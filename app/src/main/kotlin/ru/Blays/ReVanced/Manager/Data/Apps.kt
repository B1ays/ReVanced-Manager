package ru.Blays.ReVanced.Manager.Data

import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.java.KoinJavaComponent.get
import ru.Blays.ReVanced.Manager.Repository.MicroGVersionsRepository
import ru.Blays.ReVanced.Manager.Repository.VersionsRepository
import ru.Blays.ReVanced.Manager.Repository.YoutubeMusicVersionsRepository
import ru.Blays.ReVanced.Manager.Repository.YoutubeVersionsRepository
import ru.blays.revanced.Elements.Elements.VectorImages.AppsIcons
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.Microg
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.MusicMonochrome
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.YoutubeMonochrome

enum class Apps {

    YOUTUBE {
        override val icon = AppsIcons.YoutubeMonochrome
        override val repository: YoutubeVersionsRepository = get(YoutubeVersionsRepository::class.java)
    },

    YOUTUBE_MUSIC {
        override val icon = AppsIcons.MusicMonochrome
        override val repository: YoutubeMusicVersionsRepository = get(YoutubeMusicVersionsRepository::class.java)
    },

    MICROG {
        override val icon = AppsIcons.Microg
        override val repository: MicroGVersionsRepository = get(MicroGVersionsRepository::class.java)
    };

    abstract val icon: ImageVector
    abstract val repository: VersionsRepository

}