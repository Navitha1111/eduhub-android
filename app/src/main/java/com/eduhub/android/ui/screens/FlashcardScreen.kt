package com.eduhub.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.FlashcardViewModel

@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel,
    onNavigateBack: () -> Unit
) {
    val currentCard by viewModel.currentCard.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val isFlipped by viewModel.isFlipped.collectAsState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Language Flashcards",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card #${currentIndex + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EduCyan400
                )
                Text(
                    text = currentCard?.languageName ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = EduAmber500,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Flip Card Container
            if (currentCard != null) {
                val card = currentCard!!
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { viewModel.flipCard() },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped) EduSlate900 else EduSlate900
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flip,
                                contentDescription = "Flip",
                                tint = if (isFlipped) EduEmerald500 else EduCyan400,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (isFlipped) "💡 ANSWER & CONCEPTS" else "❓ MEMORY RECALL QUESTION",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isFlipped) EduEmerald500 else EduCyan400,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isFlipped) card.answer else card.question,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontFamily = if (isFlipped && card.answer.contains("\n")) FontFamily.Monospace else FontFamily.Default,
                                fontSize = if (isFlipped && card.answer.contains("\n")) 13.sp else 20.sp,
                                lineHeight = 26.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "👆 Tap card to flip",
                                style = MaterialTheme.typography.labelSmall,
                                color = EduSlate400
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.prevCard() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Previous")
                }

                Button(
                    onClick = { viewModel.nextCard() },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                ) {
                    Text("Next Card", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }
        }
    }
}
