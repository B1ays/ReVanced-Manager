package ru.Blays.ReVanced.Manager.UI.Screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import ru.blays.revanced.Elements.Util.getStringRes
import ru.blays.revanced.Presentation.R
import ru.hh.toolbar.custom_toolbar.CollapsingTitle
import ru.hh.toolbar.custom_toolbar.CustomToolbar

@Destination
@Composable
fun AboutScreen(
    navController: NavController
) {
    Scaffold(
       /* modifier = Modifier
            .nestedScroll(
                scrollBehavior.nestedScrollConnection
            ),*/
        topBar = {
            CustomToolbar(
                collapsingTitle = CollapsingTitle.large(titleText = getStringRes(R.string.AppBar_About)),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "NavigateBack")
                    }
                }/*,
                scrollBehavior = scrollBehavior*/
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(
                    padding.calculateTopPadding()
                )
        ) {
            items(5) {
                Text(text = "item $it")
            }
        }
    }
}