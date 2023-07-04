package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import ru.Blays.ReVanced.Manager.BuildConfig
import ru.blays.revanced.Elements.Elements.LazyItems.itemsGroupWithHeader
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.AuthorCard
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.ContactsCards
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.CreditCards
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.HeadItem
import ru.blays.revanced.Elements.GlobalState.NavBarState
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

@Destination
@Composable
fun AboutScreen(
    navController: NavController
) {

    val scrollBehavior = rememberToolbarScrollBehavior()

    val lazyListState = rememberLazyListState()

    if (!lazyListState.canScrollForward && lazyListState.canScrollBackward) NavBarState.shouldHideNavigationBar = true
    else if (!lazyListState.canScrollForward && !lazyListState.canScrollBackward) NavBarState.shouldHideNavigationBar = false
    else NavBarState.shouldHideNavigationBar = false

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_About)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "NavigateBack",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = .8F)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(
                    scrollBehavior.nestedScrollConnection
                )
                .padding(
                    top = padding.calculateTopPadding()
                )
                .fillMaxSize(),
            state = lazyListState
        ) {

            itemsGroupWithHeader(title = getStringRes(R.string.about_group_information)) {
                HeadItem(
                    appIco = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                    appName = stringResource(R.string.app_name),
                    versionName = BuildConfig.VERSION_NAME,
                    buildType = BuildConfig.BUILD_TYPE
                )
            }

            itemsGroupWithHeader(title = getStringRes(R.string.about_group_author)) {
                AuthorCard()
                ContactsCards()
            }

            itemsGroupWithHeader(title = getStringRes(R.string.about_group_credits)) {
                CreditCards()
            }
        }
    }
}

