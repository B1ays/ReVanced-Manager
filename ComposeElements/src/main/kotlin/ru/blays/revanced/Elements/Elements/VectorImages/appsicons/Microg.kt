package ru.blays.revanced.Elements.Elements.VectorImages.appsicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Elements.Elements.VectorImages.AppsIcons

val AppsIcons.Microg: ImageVector
    get() {
        if (_microg != null) {
            return _microg!!
        }
        _microg = Builder(name = "Microg", defaultWidth = 167.2235.dp, defaultHeight = 168.0203.dp,
                viewportWidth = 167.2235f, viewportHeight = 168.0203f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(166.47f, 100.0f)
                curveToRelative(-3.47f, 33.0f, -38.47f, 69.0f, -82.394f, 68.0f)
                curveTo(39.099f, 166.976f, 2.348f, 133.233f, 0.107f, 88.3f)
                curveTo(-2.303f, 39.957f, 36.185f, 0.0f, 84.0f, 0.0f)
                curveToRelative(20.762f, 0.0f, 39.758f, 7.529f, 54.409f, 20.008f)
                curveToRelative(1.333f, 1.136f, 1.411f, 3.172f, 0.172f, 4.411f)
                lineToRelative(-19.173f, 19.173f)
                curveToRelative(-1.047f, 1.047f, -2.7f, 1.172f, -3.89f, 0.292f)
                curveToRelative(-9.17f, -6.784f, -20.716f, -10.541f, -33.157f, -9.788f)
                curveToRelative(-25.018f, 1.514f, -45.383f, 21.59f, -47.221f, 46.586f)
                curveToRelative(-2.177f, 29.615f, 21.207f, 54.319f, 50.359f, 54.319f)
                curveToRelative(21.164f, 0.0f, 39.287f, -13.02f, 46.794f, -31.492f)
                curveToRelative(0.68f, -1.674f, -0.521f, -3.508f, -2.328f, -3.508f)
                horizontalLineToRelative(-41.967f)
                curveToRelative(-1.657f, 0.0f, -3.0f, -1.343f, -3.0f, -3.0f)
                verticalLineToRelative(-25.0f)
                curveToRelative(0.0f, -1.657f, 1.343f, -3.0f, 3.0f, -3.0f)
                horizontalLineToRelative(75.797f)
                curveToRelative(1.6f, 0.0f, 2.917f, 1.248f, 2.996f, 2.846f)
                curveToRelative(0.369f, 7.509f, 0.881f, 16.704f, -0.323f, 28.154f)
                close()
            }
        }
        .build()
        return _microg!!
    }

private var _microg: ImageVector? = null
