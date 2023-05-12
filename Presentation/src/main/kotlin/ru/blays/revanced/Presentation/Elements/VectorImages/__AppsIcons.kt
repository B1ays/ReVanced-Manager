package ru.blays.revanced.Presentation.Elements.VectorImages

import androidx.compose.ui.graphics.vector.ImageVector
import ru.blays.revanced.Presentation.Elements.VectorImages.appsicons.MusicMonochrome
import ru.blays.revanced.Presentation.Elements.VectorImages.appsicons.YoutubeMonochrome
import kotlin.collections.List as ____KtList

public object AppsIcons

private var __AllIcons: ____KtList<ImageVector>? = null

val AppsIcons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(MusicMonochrome, YoutubeMonochrome)
    return __AllIcons!!
  }
