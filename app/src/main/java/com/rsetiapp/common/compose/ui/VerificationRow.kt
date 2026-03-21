package com.rsetiapp.common.compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rsetiapp.common.compose.model.VerificationItem

/**
 * Created by Rishi Porwal
 */

@Composable
fun VerificationRow(item: VerificationItem) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        InfoRow(item.label, item.value)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            YesNoChip(
                text = "Yes",
                isSelected = item.answer == "YES",
                selectedColor = Color(0xFF2E7D32)
            ) {
                item.answer = "YES"
                item.remark = ""
            }

            YesNoChip(
                text = "No",
                isSelected = item.answer == "NO",
                selectedColor = Color(0xFFC62828)
            ) {
                item.answer = "NO"
            }
        }

        if (item.answer == "NO") {
            OutlinedTextField(
                value = item.remark,
                onValueChange = { item.remark = it },
                placeholder = { Text("Enter remark") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Divider()
    }
}