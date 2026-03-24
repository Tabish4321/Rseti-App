package com.rsetiapp.common.compose.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.elevatedCardColors
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rsetiapp.common.compose.model.VerificationItem

/**
 * Created by Rishi Porwal
 */

@Composable
fun ComplianceItemCard(
    item: VerificationItem,
    modifier: Modifier = Modifier
) {

    // 🔥 IMPORTANT: Local state for recomposition
    var answer by remember { mutableStateOf(item.answer) }
    var remark by remember { mutableStateOf(item.remark) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.elevatedCardElevation(5.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // 🔹 Header
            InfoRowCard(
                label = item.label,
                value = item.value
            )

            Divider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            Text(
                text = "Is this valid?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 🔹 YES / NO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                YesNoChip(
                    text = "Yes",
                    isSelected = answer == "YES",
                    selectedColor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                ) {
                    answer = "YES"
                    remark = ""
                    item.answer = "YES"
                    item.remark = ""
                }

                YesNoChip(
                    text = "No",
                    isSelected = answer == "NO",
                    selectedColor = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                ) {
                    answer = "NO"
                    item.answer = "NO"
                }
            }

            // 🔹 REMARK FIELD (ONLY IF NO)
            AnimatedVisibility(visible = answer == "NO") {

                Column {

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = remark,
                        onValueChange = {
                            remark = it
                            item.remark = it
                        },
                        placeholder = { Text("Enter remark") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFC62828),
                            cursorColor = Color(0xFFC62828)
                        )
                    )
                }
            }
        }
    }
}