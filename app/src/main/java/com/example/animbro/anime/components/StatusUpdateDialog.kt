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
import androidx.compose.ui.tooling.preview.Preview
import com.example.animbro.ui.theme.AnimBroTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color

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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { category ->
                    val isSelected = (category == currentStatus)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = if (isSelected) 4.dp else 0.dp,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onStatusSelected(category) }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = category,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
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

@Preview(showBackground = true)
@Composable
fun StatusUpdateDialogPreview() {
    AnimBroTheme {
        StatusUpdateDialog(
            currentStatus = "Watching",
            onDismissRequest = {},
            onStatusSelected = {},
            onRemoveClick = {}
        )
    }
}
