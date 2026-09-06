package com.example.roamablenew.ui.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.roamablenew.data.AccessibilityTag
import com.example.roamablenew.data.AccessibilityTagType
import com.example.roamablenew.data.Location
import kotlin.collections.groupingBy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailSheet(
    location: Location,
    tags: List<AccessibilityTag>,
    onDismiss: () -> Unit,
    onAddInfoClick: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = location.imageUrl,
                contentDescription = location.name,
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
            Text(location.name, style = MaterialTheme.typography.headlineSmall)
            location.address?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            Spacer(Modifier.height(8.dp))
            location.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

            Spacer(Modifier.height(16.dp))
            Text("Accessibility Info", style = MaterialTheme.typography.titleMedium)

            val grouped = tags.groupingBy { it.tagType }.eachCount()
            if (grouped.isEmpty()) {
                Text("No accessibility info yet.", style = MaterialTheme.typography.bodySmall)
            } else {
                grouped.forEach { (type, count) ->
                    val label = AccessibilityTagType.entries.find { it.name == type }?.label ?: type
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("$label — reported by $count user${if (count != 1) "s" else ""}")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = onAddInfoClick, modifier = Modifier.fillMaxWidth()) {
                Text("Add accessibility info")
            }
        }
    }
}