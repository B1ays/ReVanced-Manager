package ru.Blays.ReVanced.Manager.Utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.runtime.Composable

@ChecksSdkIntAtLeast
fun isSAndAbove(block: () -> Unit) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) block() else Unit

@SuppressLint("ComposableNaming")
@ChecksSdkIntAtLeast
@Composable
fun isSAndAboveCompose(block: @Composable () -> Unit) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) block() else Unit