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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.outlinedCardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

/**
 * Created by Rishi Porwal
 */
@Composable
fun YesNoChip(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val backgroundColor = Color.White

    val borderColor = if (isSelected) {
        selectedColor
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    val contentColor = if (isSelected) {
        selectedColor
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    OutlinedCard(
        onClick = { onClick() }, // ✅ same logic
        modifier = modifier
            .height(44.dp), // 🔥 better touch size
        shape = RoundedCornerShape(8.dp), // more modern pill
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.outlinedCardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.outlinedCardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔹 Selection Indicator (Improved)
            if (isSelected) {

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(selectedColor, CircleShape)
                )

                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}