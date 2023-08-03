package ru.blays.revanced.Elements.Elements.CustomButton

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.blays.revanced.Elements.Elements.CustomSurface.CustomSurface

@Composable
fun CustomIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    minSize: Dp = 50.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8F),
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    shadowElevation: Dp = 0.dp,
    shadowColor: Color = DefaultShadowColor,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = PaddingValues(6.dp),
    content: @Composable RowScope.() -> Unit
) {
    CustomSurface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = border,
        shadowElevation = shadowElevation,
        shadowColor = shadowColor
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                Modifier
                    .defaultMinSize(minSize, minSize)
                    .padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

@Composable
fun BackgroundIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    shape: Shape = ButtonDefaults.shape,
    minSize: Dp = 50.dp,
    iconScale: Float = 1F,
    containerColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8F),
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentPadding: PaddingValues = PaddingValues(6.dp),
) {
    Box(
        modifier = modifier
            .background(
                color = containerColor,
                shape = shape
            )
    ) {
        Icon(
            modifier = Modifier
                .defaultMinSize(minSize, minSize)
                .padding(contentPadding)
                .scale(iconScale),
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
    }
}