package ru.blays.revanced.Presentation.Elements.VectorImages.appsicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Presentation.Elements.VectorImages.AppsIcons

public val AppsIcons.MusicMonochrome: ImageVector
    get() {
        if (_musicMonochrome != null) {
            return _musicMonochrome!!
        }
        _musicMonochrome = Builder(name = "MusicMonochrome", defaultWidth = 192.0.dp, defaultHeight
                = 192.0.dp, viewportWidth = 192.0f, viewportHeight = 192.0f).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(96.0f, 14.0f)
                curveToRelative(45.21f, 0.0f, 82.0f, 36.79f, 82.0f, 82.0f)
                curveToRelative(0.0f, 45.21f, -36.79f, 82.0f, -82.0f, 82.0f)
                curveToRelative(-45.21f, 0.0f, -82.0f, -36.79f, -82.0f, -82.0f)
                curveTo(14.0f, 50.79f, 50.79f, 14.0f, 96.0f, 14.0f)
                moveToRelative(0.0f, -14.0f)
                curveTo(42.98f, 0.0f, 0.0f, 42.98f, 0.0f, 96.0f)
                curveToRelative(0.0f, 53.02f, 42.98f, 96.0f, 96.0f, 96.0f)
                curveToRelative(53.02f, 0.0f, 96.0f, -42.98f, 96.0f, -96.0f)
                curveTo(192.0f, 42.98f, 149.02f, 0.0f, 96.0f, 0.0f)
                horizontalLineToRelative(0.0f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFFFFFF)),
                    strokeLineWidth = 14.0f, strokeLineCap = Round, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(73.25f, 146.0f)
                lineToRelative(72.95f, -42.12f)
                curveToRelative(6.07f, -3.5f, 6.07f, -12.27f, -0.0f, -15.77f)
                curveToRelative(-24.32f, -14.04f, -48.63f, -28.08f, -72.95f, -42.12f)
            }
            path(fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(120.46f, 90.19f)
                lineToRelative(-42.52f, -24.55f)
                curveToRelative(-4.48f, -2.58f, -10.07f, 0.65f, -10.07f, 5.81f)
                verticalLineToRelative(49.1f)
                curveToRelative(0.0f, 5.17f, 5.59f, 8.4f, 10.07f, 5.81f)
                lineToRelative(42.52f, -24.55f)
                curveToRelative(4.48f, -2.58f, 4.48f, -9.04f, 0.0f, -11.63f)
                close()
            }
        }
        .build()
        return _musicMonochrome!!
    }

private var _musicMonochrome: ImageVector? = null
