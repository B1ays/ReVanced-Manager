package ru.blays.revanced.Services.NonRoot.Util

import android.os.Build

internal fun getLatestOrProvidedAppVersion(
    version: String,
    appVersions: List<String>?
): String {
    if (appVersions == null)
        return version

    if (appVersions.contains(version))
        return version

    return appVersions.last()
}

internal val arch
    get() = when {
        Build.SUPPORTED_ABIS.contains("x86") -> "x86"
        Build.SUPPORTED_ABIS.contains("arm64-v8a") -> "arm64_v8a"
        else -> "armeabi_v7a"
    }

