package com.eduhub.android.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.theme.*

@Composable
fun CodeSyntaxCard(
    code: String,
    language: String = "Code",
    title: String = "Interactive Code Example",
    onRunInPlayground: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CodeBg)
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduSlate900)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Traffic light dots
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(EduRose500))
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(EduAmber500))
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(EduEmerald500))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = EduSlate400,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Chip
                    Surface(
                        color = EduSlate800,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = language.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = EduCyan400,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Copy Button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Code", code)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = EduSlate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Playground Button
                    if (onRunInPlayground != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onRunInPlayground,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run in Playground",
                                tint = EduEmerald500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Code Content with Line Numbers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(12.dp)
            ) {
                val lines = code.lines()
                // Line Numbers
                Column {
                    lines.indices.forEach { index ->
                        Text(
                            text = "${index + 1}".padStart(2, ' '),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = EduSlate600,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Source Code Lines
                Column {
                    lines.forEach { line ->
                        Text(
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = EduSlate50,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
