package ru.Blays.ReVanced.Manager.Data

import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.java.KoinJavaComponent.get
import ru.Blays.ReVanced.Manager.Repository.AppRepositiry.AppRepository
import ru.Blays.ReVanced.Manager.Repository.AppRepositiry.AppRepositoryInterface
import ru.blays.revanced.Elements.Elements.VectorImages.AppsIcons
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.Microg
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.MusicMonochrome
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.YoutubeMonochrome
import ru.blays.revanced.Services.PublicApi.PackageManagerApi
import ru.blays.revanced.Services.RootService.Util.MagiskInstaller
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

enum class Apps {

    YOUTUBE {
        override val icon = AppsIcons.YoutubeMonochrome
        override val repository = AppRepository.builder {
            appName = "YouTube ReVanced"
            appType = GetVersionsListUseCase.YOUTUBE
            moduleType = MagiskInstaller.Module.YOUTUBE
            getVersionsListUseCase = getVersionsListUС
            version {
                versionName = "Non-Root"
                packageName = "app.revanced.android.youtube"
                localVersionSource = {
                    localVersionSourceImpl(packageName!!)
                }
                remoteVersionSource = {
                    remoteVersionsList.firstOrNull()?.version
                }
            }
            version {
                versionName = "Root"
                packageName = "com.google.android.youtube"
                isRootNeeded = true
                localVersionSource = {
                    localVersionSourceImpl(packageName!!)
                }
                remoteVersionSource = {
                    remoteVersionsList.firstOrNull()?.version
                }
            }
        }
    },

    YOUTUBE_MUSIC {
        override val icon = AppsIcons.MusicMonochrome
        override val repository = AppRepository.builder {
            appName = "YouTube Music ReVanced"
            appType = GetVersionsListUseCase.MUSIC
            moduleType = MagiskInstaller.Module.YOUTUBE_MUSIC
            getVersionsListUseCase = getVersionsListUС

            // Non-Root version
            version {
                versionName = "Non-Root"
                packageName = "app.revanced.android.apps.youtube.music"
                localVersionSource = {
                    localVersionSourceImpl(packageName!!)
                }
                remoteVersionSource = {
                    remoteVersionsList.firstOrNull()?.version
                }
            }
            // Root version
            version {
                versionName = "Root"
                packageName = "com.google.android.apps.youtube.music"
                isRootNeeded = true
                localVersionSource = {
                    localVersionSourceImpl(packageName!!)
                }
                remoteVersionSource = {
                    remoteVersionsList.firstOrNull()?.version
                }
            }
        }
    },

    MICROG {
        override val icon = AppsIcons.Microg
        override val repository = AppRepository.builder {
            appName = "MMicroG"
            appType = GetVersionsListUseCase.MICROG
            getVersionsListUseCase = getVersionsListUС

            version {
                versionName = ""
                packageName = "com.mgoogle.android.gms"
                localVersionSource = {
                    localVersionSourceImpl(packageName!!)
                }
                remoteVersionSource = {
                    remoteVersionsList.firstOrNull()?.version
                }
            }
        }
    };

    abstract val icon: ImageVector
    abstract val repository: AppRepositoryInterface

    private val packageManager: PackageManagerApi = get(PackageManagerApi::class.java)

    protected val getVersionsListUС: GetVersionsListUseCase = get(GetVersionsListUseCase::class.java)

    protected val localVersionSourceImpl: (suspend (packageName: String) -> String?) = { packageName ->
        packageManager
            .getVersionName(packageName)
            .await()
            .getValueOrNull()
    }
}