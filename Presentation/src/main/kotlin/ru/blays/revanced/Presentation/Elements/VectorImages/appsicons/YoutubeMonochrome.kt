package ru.blays.revanced.Presentation.Elements.VectorImages.appsicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Presentation.Elements.VectorImages.AppsIcons


val AppsIcons.YoutubeMonochrome: ImageVector
    get() {
        if (_youtubeMonochrome != null) {
            return _youtubeMonochrome!!
        }
        _youtubeMonochrome = Builder(name = "YoutubeMonochrome", defaultWidth = 192.0.dp,
                defaultHeight = 192.0.dp, viewportWidth = 192.0f, viewportHeight = 192.0f).apply {
            path(
                fill = SolidColor(Color.White), stroke = null, strokeLineWidth = 0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(160.33f, 183.23f)
                lineToRelative(-40.89f, -66.74f)
                curveToRelative(-1.54f, -2.53f, -0.41f, -5.82f, 2.36f, -6.88f)
                curveToRelative(22.82f, -8.73f, 38.76f, -31.88f, 36.4f, -58.07f)
                curveTo(155.56f, 22.13f, 130.14f, 0.0f, 100.62f, 0.0f)
                horizontalLineTo(35.69f)
                curveToRelative(-2.61f, 0.0f, -4.72f, 2.11f, -4.72f, 4.72f)
                verticalLineTo(187.28f)
                curveToRelative(0.0f, 2.61f, 2.11f, 4.72f, 4.72f, 4.72f)
                horizontalLineToRelative(26.75f)
                curveToRelative(2.61f, 0.0f, 4.72f, -2.11f, 4.72f, -4.72f)
                verticalLineTo(117.31f)
                curveToRelative(0.0f, -3.16f, 4.15f, -4.34f, 5.82f, -1.65f)
                curveToRelative(14.92f, 24.17f, 29.82f, 48.35f, 44.73f, 72.52f)
                curveToRelative(0.87f, 1.4f, 2.38f, 2.25f, 4.01f, 2.25f)
                horizontalLineToRelative(34.58f)
                curveToRelative(3.7f, 0.0f, 5.96f, -4.04f, 4.03f, -7.19f)
                close()
                moveTo(77.12f, 87.74f)
                curveToRelative(-5.11f, 2.97f, -11.54f, -0.72f, -11.54f, -6.64f)
                verticalLineTo(27.49f)
                curveToRelative(0.0f, -5.92f, 6.42f, -9.62f, 11.54f, -6.64f)
                lineToRelative(46.24f, 26.79f)
                curveToRelative(5.11f, 2.97f, 5.11f, 10.34f, 0.0f, 13.31f)
                lineToRelative(-46.24f, 26.79f)
                close()
            }
        }
        .build()
        return _youtubeMonochrome!!
    }

private var _youtubeMonochrome: ImageVector? = null
