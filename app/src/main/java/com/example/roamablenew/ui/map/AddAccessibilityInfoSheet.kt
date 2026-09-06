package com.example.roamablenew.ui.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.roamablenew.data.AccessibilityTag
import com.example.roamablenew.data.AccessibilityTagType
import kotlin.collections.filter

@Composable
fun AddAccessibilityInfoSheet(
    allTags: List<AccessibilityTag>,
    currentUserEmail: String,
    onToggle: (tagType: String, isChecked: Boolean) -> Unit
) {
    val userTaggedTypes = remember(allTags, currentUserEmail) {
        allTags.filter { it.userId == currentUserEmail }.map { it.tagType }.toSet()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add accessibility info", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        AccessibilityTagType.entries.forEach { type ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = type.name in userTaggedTypes,
                    onCheckedChange = { checked -> onToggle(type.name, checked) }
                )
                Text(type.label)
            }
        }
    }
}