package com.eduhub.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.theme.*

@Composable
fun AudioNarratorBar(
    isSpeaking: Boolean,
    currentRate: Float,
    onPlayPause: () -> Unit,
    onStop: () -> Unit,
    onCycleRate: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = EduSlate900,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSpeaking) EduCyan500 else EduSlate800),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Audiobook Mode",
                        tint = if (isSpeaking) Color.Black else EduCyan400,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (isSpeaking) "🎙️ Audio Narrator Active" else "🎧 Hands-Free Audiobook Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isSpeaking) "Listening to full lesson content" else "Listen to lesson while walking/resting",
                        style = MaterialTheme.typography.bodySmall,
                        color = EduSlate400,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rate Toggle (1.0x, 1.25x, 1.5x, 0.75x)
                TextButton(
                    onClick = onCycleRate,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${currentRate}x",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EduCyan400
                    )
                }

                // Play / Stop Button
                IconButton(
                    onClick = if (isSpeaking) onStop else onPlayPause,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSpeaking) EduRose500 else EduCyan500)
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isSpeaking) "Stop" else "Listen",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
