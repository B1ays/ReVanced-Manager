package ru.blays.revanced.Elements.Elements.LazyItems

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


fun LazyListScope.itemsGroupWithHeader(title: String, content: @Composable () -> Unit) = item {

    Text(
        modifier = Modifier
            .padding(12.dp),
        text = title,
        style = MaterialTheme.typography.titleLarge
    )
    content()

    Spacer(modifier = Modifier.height(16.dp))
}

