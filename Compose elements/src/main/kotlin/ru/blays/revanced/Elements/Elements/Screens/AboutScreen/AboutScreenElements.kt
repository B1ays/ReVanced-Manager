package ru.blays.revanced.Elements.Elements.Screens.AboutScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.blays.revanced.Elements.DataClasses.CardShape
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.Elements.Util.getStringRes
import ru.blays.revanced.Presentation.R


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
fun ContactsCards() {

    val contactList = Contact.list

    contactList.forEachIndexed { index, item ->
        if (index < contactList.lastIndex) {
            ContactItem(contact = item, CardShape.CardMid)
        } else if (index == contactList.lastIndex) {
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
fun CreditCards() {

    val list = Credits.list

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

            TextButton(
                onClick = { context.startActivity(intent) }
            ) {
                Text(
                    text = "${getStringRes(id = R.string.about_Iam)} ${contact.name}",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreditCard(item: Credits, shape: Shape) {

    val intent = Intent(Intent.ACTION_VIEW, item.linkUri)

    val context = LocalContext.current

    Card(
        onClick = { context.startActivity(intent) },
        modifier = Modifier
            .padding(
                horizontal = DefaultPadding.CardHorizontalPadding,
                vertical = DefaultPadding.CardVerticalPadding
            )
            .fillMaxWidth(),
        shape = shape
    ) {

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = item.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = "${stringResource(id = R.string.about_credits_for)} ${item.reason}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

    }
}

data class Contact(
    val iconID: Int,
    val name: String,
    val link: String
) {
    companion object {
        val list = listOf(
            Contact(iconID = R.drawable.ic_telegram, getStringRes(R.string.about_social_telegram), "https://t.me/B1ays"),
            Contact(iconID = R.drawable.ic_4pda, getStringRes(R.string.about_social_4pda), "https://4pda.to/forum/index.php?showuser=7576426")
        )
    }
}

data class Credits(
    val name: String,
    val reason: String,
    val linkUri: Uri
) {
    companion object {
        val list = listOf(
            Credits(name = "hh.ru", reason = getStringRes(R.string.about_credits_reason_hh), linkUri = Uri.parse("https://github.com/hhru/hh-histories-compose-custom-toolbar")),
            Credits(name = "iTaysonLab", reason = getStringRes(R.string.about_credits_reason_iTaysonLab), linkUri = Uri.parse("https://github.com/iTaysonLab/jetisteam")),
            Credits(name = "Material Foundation", reason = getStringRes(R.string.about_credits_reason_material_foundation), linkUri = Uri.parse("https://github.com/material-foundation/material-color-utilities")),
            Credits(name = "Vanced Team", reason = getStringRes(R.string.about_credits_reason_teamVanced), linkUri = Uri.parse("https://github.com/TeamVanced/VancedManager")),
        )
    }
}

