package com.example.animbro.anime.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animbro.R

@Composable
fun StatusUpdateDialog(
    currentStatus: String?, // Can be null if the anime is not in any list
    onDismissRequest: () -> Unit,
    onStatusSelected: (String) -> Unit,
    onRemoveClick: () -> Unit // Extra feature: Allow removing from list
) {
    val categories = listOf("Watching", "Completed", "Dropped", "Pending")

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Add to List",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onStatusSelected(category) } // Clicking row selects it
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (category == currentStatus),
                            onClick = { onStatusSelected(category) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category,
                            fontSize = 16.sp,
                            fontWeight = if (category == currentStatus) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {
            // Only show "Remove" if the anime is currently in a list
            if (currentStatus != null) {
                TextButton(
                    onClick = onRemoveClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove from List")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}
