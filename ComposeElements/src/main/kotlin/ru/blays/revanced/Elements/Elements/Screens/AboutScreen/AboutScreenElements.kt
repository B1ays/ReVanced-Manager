package ru.blays.revanced.Elements.Elements.Screens.AboutScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import ru.blays.revanced.Elements.DataClasses.DefaultPadding
import ru.blays.revanced.shared.R
import ru.blays.revanced.shared.Util.getStringRes

@Composable
fun ContactItem(contact: Contact, cardShape: RoundedCornerShape) {

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
fun AboutCardWithIcoAndLink(context: Context, ico: ImageVector, linkText: String, intent: Intent, shape: Shape) {

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
fun CreditCard(item: Credits, shape: Shape) {

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

data class Credits(
    val name: String,
    val reason: String,
    val linkUri: Uri
)

data class Contact(
    val iconID: Int,
    val name: String,
    val link: String
)