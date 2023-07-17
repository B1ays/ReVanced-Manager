package ru.Blays.ReVanced.Manager.UI.Screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.Blays.ReVanced.Manager.BuildConfig
import ru.Blays.ReVanced.Manager.UI.Navigation.shouldHideNavigationBar
import ru.blays.helios.androidx.AndroidScreen
import ru.blays.helios.navigator.LocalNavigator
import ru.blays.helios.navigator.currentOrThrow
import ru.blays.revanced.Elements.DataClasses.CardShape
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.Elements.LazyItems.itemsGroupWithHeader
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.AboutCardWithIcoAndLink
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.Contact
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.ContactItem
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.CreditCard
import ru.blays.revanced.Elements.Elements.Screens.AboutScreen.Credits
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

class AboutScreen: AndroidScreen() {

    @Composable
    override fun Content() {
        val contactsList = listOf(
            Contact(iconID = R.drawable.ic_telegram, getStringRes(R.string.about_social_telegram), "https://t.me/B1ays"),
            Contact(iconID = R.drawable.ic_4pda, getStringRes(R.string.about_social_4pda), "https://4pda.to/forum/index.php?showuser=7576426")
        )

        val creditsList = listOf(
            Credits(name = "hh.ru", reason = getStringRes(R.string.about_credits_reason_hh), linkUri = Uri.parse("https://github.com/hhru/hh-histories-compose-custom-toolbar")),
            Credits(name = "iTaysonLab", reason = getStringRes(R.string.about_credits_reason_iTaysonLab), linkUri = Uri.parse("https://github.com/iTaysonLab/jetisteam")),
            Credits(name = "Material Foundation", reason = getStringRes(R.string.about_credits_reason_material_foundation), linkUri = Uri.parse("https://github.com/material-foundation/material-color-utilities")),
            Credits(name = "Vanced Team", reason = getStringRes(R.string.about_credits_reason_teamVanced), linkUri = Uri.parse("https://github.com/TeamVanced/VancedManager")),
        )

        val scrollBehavior = rememberToolbarScrollBehavior()

        val lazyListState = rememberLazyListState()

        val navigator = LocalNavigator.currentOrThrow

        shouldHideNavigationBar = when {
            !lazyListState.canScrollForward && lazyListState.canScrollBackward -> true
            !lazyListState.canScrollForward && !lazyListState.canScrollBackward -> false
            else -> false
        }

        Scaffold(
            topBar = {
                CustomToolbar(
                    collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_About)),
                    navigationIcon = {
                        IconButton(onClick = navigator::pop) {
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
                    ContactsCards(contactsList)
                }

                itemsGroupWithHeader(title = getStringRes(R.string.about_group_credits)) {
                    CreditCards(creditsList)
                }
            }
        }
    }
}

@Composable
fun HeadItem(appIco: ImageVector, appName: String, versionName: String, buildType: String) {

    val intentTelegram = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Blays_ReVanced_Manager"))
    val intentGitHub = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/B1ays/ReVanced-Manager/"))

    val context = LocalContext.current

    Column {

        Card(
            modifier = Modifier
                .padding(
                    horizontal = DefaultPadding.CardHorizontalPadding,
                    vertical = DefaultPadding.CardVerticalPadding
                )
                .fillMaxWidth(),
            shape = CardShape.CardStart
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Image(
                    imageVector = appIco, contentDescription = "Icon",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = appName,
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${getStringRes(R.string.Version)}: $versionName - $buildType",
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }

        AboutCardWithIcoAndLink(
            context = context,
            ico = ImageVector.vectorResource(id = R.drawable.ic_telegram),
            linkText = getStringRes(R.string.about_card_telegram),
            intent = intentTelegram,
            shape = CardShape.CardMid
        )

        AboutCardWithIcoAndLink(
            context = context,
            ico = ImageVector.vectorResource(id = R.drawable.ic_github),
            linkText = getStringRes(R.string.about_card_github),
            intent = intentGitHub,
            shape = CardShape.CardEnd
        )
    }
}

@Composable
fun ContactsCards(list: List<Contact>) {
    list.forEachIndexed { index, item ->
        if (index < list.lastIndex) {
            ContactItem(contact = item, CardShape.CardMid)
        } else if (index == list.lastIndex) {
            ContactItem(contact = item, CardShape.CardEnd)
        }
    }
}

@Composable
fun AuthorCard() {
    Card(
        modifier = Modifier
            .padding(
                horizontal = DefaultPadding.CardHorizontalPadding,
                vertical = DefaultPadding.CardVerticalPadding
            )
            .fillMaxWidth(),
        shape = CardShape.CardStart
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            text = getStringRes(R.string.about_card_author)
        )
    }
}

@Composable
fun CreditCards(list: List<Credits>) {
    list.forEachIndexed() { index, item ->
        if (index == 0) {
            CreditCard(item = item, shape = CardShape.CardStart)
        } else if (index < list.lastIndex) {
            CreditCard(item = item, shape = CardShape.CardMid)
        } else if (index == list.lastIndex) {
            CreditCard(item = item, shape = CardShape.CardEnd)
        }
    }
}