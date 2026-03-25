package com.rsetiapp.common.compose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun VerificationHeader(lat: Double?, lng: Double?) {

    Surface(
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = "Verification",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (lat != null && lng != null) {

                Text(
                    text = "Current Location- Lat: %.5f, Lng: %.5f".format(lat, lng),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32)
                )

            } else {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Fetching location...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}