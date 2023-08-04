package ru.Blays.ReVanced.Manager.Data

import androidx.compose.ui.graphics.vector.ImageVector
import org.koin.java.KoinJavaComponent.get
import ru.Blays.ReVanced.Manager.Repository.AppRepositiry.AppRepository
import ru.Blays.ReVanced.Manager.Repository.AppRepositiry.AppRepositoryInterface
import ru.blays.revanced.DeviceUtils.PublicApi.PackageManagerApi
import ru.blays.revanced.DeviceUtils.Root.ModuleIntstaller.ModuleInstaller
import ru.blays.revanced.Elements.Elements.VectorImages.AppsIcons
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.Microg
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.MusicMonochrome
import ru.blays.revanced.Elements.Elements.VectorImages.appsicons.YoutubeMonochrome
import ru.blays.revanced.domain.UseCases.GetVersionsListUseCase

enum class Apps {

    YOUTUBE {
        override val icon = AppsIcons.YoutubeMonochrome
        override val repository = AppRepository.builder {
            appName = "YouTube ReVanced"
            catalogUrl = "https://github.com/B1ays/ReVanced-Versions-Catalog/raw/main/YouTube/VersionsList.json"
            moduleType = ModuleInstaller.Module.YOUTUBE
            getVersionsListUseCase = getVersionsListUС
            version {
                versionName = "Non-Root"
                packageName = "app.rvx.android.youtube"
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
            catalogUrl = "https://github.com/B1ays/ReVanced-Versions-Catalog/raw/main/YouTube%20Music/VersionsList.json"
            moduleType = ModuleInstaller.Module.YOUTUBE_MUSIC
            getVersionsListUseCase = getVersionsListUС

            // Non-Root version
            version {
                versionName = "Non-Root"
                packageName = "app.rvx.android.apps.youtube.music"
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
            catalogUrl = "https://github.com/B1ays/ReVanced-Versions-Catalog/raw/main/MicroG/VersionsList.json"
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
        packageManager.getVersionName(packageName).getValueOrNull()
    }
}