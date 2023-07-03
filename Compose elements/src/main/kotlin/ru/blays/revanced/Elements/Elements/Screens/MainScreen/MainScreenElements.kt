package ru.blays.revanced.Elements.Elements.Screens.MainScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import ru.blays.revanced.Elements.Elements.FloatingBottomMenu.surfaceColorAtAlpha
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoCard(
    icon: ImageVector,
    appType: String,
    appName: String,
    availableVersion: MutableStateFlow<String?>,
    rootVersion: MutableStateFlow<String?>,
    version: MutableStateFlow<String?>,
    nonRootVersion: MutableStateFlow<String?>,
    hasRootVersion: Boolean,
    isNonRootVersionInstalled: MutableStateFlow<Boolean>?,
    isModuleInstalled: MutableStateFlow<Boolean>?,
    actionNavigateToVersionsListScreen: () -> Unit
) {

    // App icon default size
    val iconSize = 80.dp

    // Content
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = actionNavigateToVersionsListScreen,
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = -iconSize / 2)
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(text = appName, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Check variables non null. If null -> default text; if notNull -> split version info for nonRoot and RootVersion
                    if (hasRootVersion) {

                        if (isNonRootVersionInstalled?.collectAsState()?.value  == true) nonRootVersion.collectAsState().value?.let {
                            Text(text = "${getStringRes(R.string.NonRoot_Version)}: $it")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (isModuleInstalled?.collectAsState()?.value == true) rootVersion.collectAsState().value?.let {
                            Text(text = "${getStringRes(R.string.Root_Version)}: $it")
                        }
                    } else {

                        // Text elements with null check using let function
                        version.collectAsState().value?.let {
                            Text(text = "${getStringRes(R.string.Installed_version)}: $it")
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    availableVersion.collectAsState().value?.let { Text(text = "${getStringRes(R.string.Available_version)}: $it") }
                }
            }

            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.round_arrow_forward_ios_24),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
    }
}


@Composable
fun AppCardRedesignRoot(
    cornerRadius: Dp = 20.dp,
    icon: ImageVector,
    appName: String,
    availableVersion: MutableStateFlow<String?>,
    rootVersion: MutableStateFlow<String?>,
    nonRootVersion: MutableStateFlow<String?>,
    onClick: () -> Unit
) {

    val availableVersionState by availableVersion.collectAsState()
    val rootVersionState by rootVersion.collectAsState()
    val nonRootVersionState by nonRootVersion.collectAsState()


    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier
            .height(145.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceTint.copy(.1F),
                shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
            )
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .blur(radius = 6.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .wrapContentSize(unbounded = true),
                imageVector = icon,
                tint = MaterialTheme.colorScheme.primary.copy(.5F),
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                availableVersionState.run {
                    AnimatedVisibility(
                        visible = this@run != null,
                        enter = slideInHorizontally(animationSpec = spring(stiffness = 300F, dampingRatio = .6F))
                    ) {
                            TextWithBackground(
                                text = "${getStringRes(R.string.Available_version)}: ${this@run}",
                                textPadding = PaddingValues(5.dp)
                            )
                        }
                    }
                Spacer(modifier = Modifier.height(5.dp))
                rootVersionState.run {
                    AnimatedVisibility(
                        visible = this@run != null,
                        enter = slideInHorizontally(animationSpec = spring())
                    ) {
                        TextWithBackground(
                            text = "${getStringRes(R.string.Root_Version)}: ${this@run}",
                            textPadding = PaddingValues(5.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                nonRootVersionState.run {
                    AnimatedVisibility(
                        visible = this@run != null,
                        enter = slideInHorizontally(animationSpec = spring())
                    ) {
                        TextWithBackground(
                            text = "${getStringRes(R.string.NonRoot_Version)}: ${this@run}",
                            textPadding = PaddingValues(5.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .height(90.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(.5F),
                text = appName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                overflow = TextOverflow.Ellipsis
            )

            Button(
                modifier = Modifier
                    .defaultMinSize(100.dp, 48.dp)
                    .padding(end = 25.dp),
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceColorAtAlpha(.2F) else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = "Open")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun AppCardRedesign(
    cornerRadius: Dp = 20.dp,
    icon: ImageVector,
    appName: String,
    availableVersion: MutableStateFlow<String?>,
    version: MutableStateFlow<String?>,
    onClick: () -> Unit
) {

    val availableVersionState by availableVersion.collectAsState()
    val versionState by version.collectAsState()


    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Box(modifier = Modifier
            .height(145.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceTint.copy(.1F),
                shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
            )
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .blur(radius = 6.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .wrapContentSize(unbounded = true)
                /*.offset(y = 40.dp)*/,
                imageVector = icon,
                tint = MaterialTheme.colorScheme.primary.copy(.5F),
                contentDescription = null,
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                availableVersionState.run {
                    AnimatedVisibility(
                        visible = this@run != null,
                        enter = slideInHorizontally(animationSpec = spring(stiffness = 300F, dampingRatio = .6F))
                    ) {
                        TextWithBackground(
                            text = "${getStringRes(R.string.Available_version)}: ${this@run}",
                            textPadding = PaddingValues(5.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                versionState.run {
                    AnimatedVisibility(
                        visible = this@run != null,
                        enter = slideInHorizontally(animationSpec = spring())
                    ) {
                        TextWithBackground(
                            text = "${getStringRes(R.string.Installed_version)}: ${this@run}",
                            textPadding = PaddingValues(5.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .height(90.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = appName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                overflow = TextOverflow.Ellipsis
            )

            Button(
                modifier = Modifier
                    /*.requiredHeight(48.dp)*/
                    .defaultMinSize(100.dp, 48.dp)
                    .padding(end = 25.dp),
                onClick = onClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceColorAtAlpha(.2F) else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = "Open")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun TextWithBackground(
    modifier: Modifier = Modifier,
    text: String,
    shape: Shape = CircleShape,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = .8F),
    textColor: Color = Color.White,
    textStyle: TextStyle = LocalTextStyle.current,
    textPadding: PaddingValues = PaddingValues(2.dp)
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = shape
            )
    ) {
        Text(
            modifier = Modifier.padding(textPadding),
            text = text,
            style = textStyle,
            color = textColor
        )
    }
}