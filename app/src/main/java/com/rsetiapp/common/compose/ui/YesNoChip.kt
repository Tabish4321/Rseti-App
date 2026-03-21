package com.rsetiapp.common.compose.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight

/**
 * Created by Rishi Porwal
 */
@Composable
fun YesNoChip(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {

    OutlinedCard(
        onClick = onClick,
        border = BorderStroke(1.dp, selectedColor),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) selectedColor.copy(alpha = 0.2f) else Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            color = selectedColor
        )
    }
}