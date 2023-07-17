package ru.blays.revanced.shared.Extensions

import androidx.compose.ui.graphics.Color

fun Color.invert(
    range: ClosedFloatingPointRange<Float> = 0F..0.2F,
    offset: Float = 0.2F
): Color {

    require(range.start >= 0F && range.endInclusive <= 1F) {
        "Invalid range"
    }
    require(offset in 0F..1F) {
        "Invalid offset"
    }

    val invertedRed = with(1F - red) colorFloat@ {
        if (this@colorFloat in range) {
            if (this@colorFloat >= 0.5F) {
                this@colorFloat - offset
            } else {
                this@colorFloat + offset
            }
        } else {
            this@colorFloat
        }
    }
    val invertedGreen = with(1F - green) colorFloat@ {
        if (this@colorFloat in range) {
            if (this@colorFloat >= 0.5F) {
                this@colorFloat - offset
            } else {
                this@colorFloat + offset
            }
        } else {
            this@colorFloat
        }
    }
    val invertedBlue = with(1F - blue) colorFloat@ {
        if (this@colorFloat in range) {
            if (this@colorFloat >= 0.5F) {
                this@colorFloat - offset
            } else {
                this@colorFloat + offset
            }
        } else {
            this@colorFloat
        }
    }
    return Color(invertedRed, invertedGreen, invertedBlue)
}