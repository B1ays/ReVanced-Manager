package ru.Blays.ReVanced.Manager.UI.Screens

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import ru.Blays.ReVanced.Manager.BuildConfig
import ru.blays.revanced.Elements.DataClasses.CardShape
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.Elements.LazyItems.itemsGroupWithHeader
import ru.blays.revanced.Elements.Util.getStringRes
import ru.blays.revanced.Presentation.R
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar
import ru.hh.toolbar.custom_toolbar.rememberToolbarScrollBehavior

@Destination
@Composable
fun AboutScreen(
    navController: NavController
) {

    val scrollBehavior = rememberToolbarScrollBehavior()

    Scaffold(
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_About)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "NavigateBack")
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
                .fillMaxSize()
        ) {

            itemsGroupWithHeader(title = getStringRes(ru.Blays.ReVanced.Manager.R.string.about_group_information)) {
                HeadItem()
            }

            itemsGroupWithHeader(title = getStringRes(ru.Blays.ReVanced.Manager.R.string.about_group_author)) {
                AboutAuthor()
            }
        }
    }
}

@Composable
private fun HeadItem() {

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
                    imageVector = ImageVector.vectorResource(id = ru.Blays.ReVanced.Manager.R.drawable.ic_launcher_foreground), contentDescription = "Icon",
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
                        text = getStringRes(ru.Blays.ReVanced.Manager.R.string.app_name),
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${getStringRes(R.string.Version)}: ${BuildConfig.VERSION_NAME} - ${BuildConfig.BUILD_TYPE}",
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }

        AboutCardWithIcoAndLink(
            context = context,
            ico = ImageVector.vectorResource(id = ru.Blays.ReVanced.Manager.R.drawable.ic_telegram),
            linkText = getStringRes(ru.Blays.ReVanced.Manager.R.string.about_card_telegram),
            intent = intentTelegram,
            shape = CardShape.CardMid
        )

        AboutCardWithIcoAndLink(
            context = context,
            ico = ImageVector.vectorResource(id = ru.Blays.ReVanced.Manager.R.drawable.ic_github),
            linkText = getStringRes(ru.Blays.ReVanced.Manager.R.string.about_card_github),
            intent = intentGitHub,
            shape = CardShape.CardEnd
        )
    }
}

@Composable
private fun AboutAuthor() {

    val contactList = ContactList().list

    AuthorNick()
    for (i in contactList.indices) {
        if (i < contactList.lastIndex) {
            ContactItem(contact = contactList[i], CardShape.CardMid)
        } else if (i == contactList.lastIndex) {
            ContactItem(contact = contactList[i], CardShape.CardEnd)
        }
    }
}

@Composable
private fun AuthorNick() {
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
            text = getStringRes(ru.Blays.ReVanced.Manager.R.string.about_card_author)
        )
    }
}

@Composable
private fun ContactItem(contact: Contact, cardShape: RoundedCornerShape) {

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contact.link))
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .fillMaxWidth(),
        shape = cardShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                imageVector = ImageVector.vectorResource(id = contact.iconID), contentDescription = "Icon",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary))

            TextButton(onClick = { context.startActivity(intent) } ) {
                Text(
                    text = "Я в ${contact.name}",
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AboutCardWithIcoAndLink(context: Context, ico: ImageVector, linkText: String, intent: Intent, shape: Shape) {

    Card(
        modifier = Modifier
            .padding(
                horizontal = DefaultPadding.CardHorizontalPadding,
                vertical = DefaultPadding.CardVerticalPadding
            )
            .fillMaxWidth(),
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Image(
                imageVector = ico, contentDescription = "Icon",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary))

            TextButton(onClick = { context.startActivity(intent) } ) {
                Text(
                    text = linkText,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            }
        }
    }
}

data class Contact(
    val iconID: Int,
    val name: String,
    val link: String
)

class ContactList {
    val list = listOf(
        /*Contact(iconID = ru.Blays.ReVanced.Manager.R.drawable.ic_vk, getStringRes(ru.Blays.ReVanced.Manager.R.string.about_social_vk), "https://vk.com/b1ays"),*/
        Contact(iconID = ru.Blays.ReVanced.Manager.R.drawable.ic_telegram, getStringRes(ru.Blays.ReVanced.Manager.R.string.about_social_telegram), "https://t.me/B1ays"),
        Contact(iconID = ru.Blays.ReVanced.Manager.R.drawable.ic_4pda, getStringRes(ru.Blays.ReVanced.Manager.R.string.about_social_4pda), "https://4pda.to/forum/index.php?showuser=7576426")
    )
}