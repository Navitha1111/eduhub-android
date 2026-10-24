package com.eduhub.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.QuizViewModel

@Composable
fun QuizScreen(
    courseSlug: String,
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(courseSlug) {
        viewModel.loadQuiz(courseSlug)
    }

    val quiz by viewModel.quiz.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val correctCount by viewModel.correctCount.collectAsState()
    val isFinished by viewModel.isQuizFinished.collectAsState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = quiz?.title ?: "Mastery Quiz",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EduCyan400)
            }
        } else if (isFinished) {
            // Quiz Results Summary Screen
            val scorePct = ((correctCount.toDouble() / questions.size) * 100).toInt()
            val isPassed = scorePct >= 60

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isPassed) "🏆" else "📚",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isPassed) "Quiz Passed with Mastery!" else "Keep Practicing!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPassed) EduEmerald500 else EduAmber500,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You scored $scorePct% ($correctCount of ${questions.size} correct)",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = EduSlate900,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isPassed) "🌟 +${scorePct * 2} XP Earned!" else "⚡ Review concepts and retry for max XP",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = EduCyan400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.restartQuiz() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🔄 Retake Quiz", color = EduCyan400, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                    ) {
                        Text("Back to Course", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            val q = questions[currentIndex]
            val options = listOf(q.optionA, q.optionB, q.optionC, q.optionD)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Question Progress Indicator
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Question ${currentIndex + 1} of ${questions.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = EduCyan400
                        )
                        Text(
                            text = "${((currentIndex.toFloat() / questions.size) * 100).toInt()}% Done",
                            style = MaterialTheme.typography.labelMedium,
                            color = EduSlate400
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / questions.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EduCyan400,
                        trackColor = EduSlate800
                    )
                }

                // Question Box
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = q.questionText,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Options List
                items(options.indices.toList()) { index ->
                    val optText = options[index]
                    val isChosen = selectedOption == index
                    val isCorrect = index == q.correctOptionIndex

                    val (bgCol, borderCol) = when {
                        isSubmitted && isCorrect -> Pair(EduEmerald500.copy(alpha = 0.2f), EduEmerald500)
                        isSubmitted && isChosen && !isCorrect -> Pair(EduRose500.copy(alpha = 0.2f), EduRose500)
                        isChosen -> Pair(EduCyan500.copy(alpha = 0.2f), EduCyan400)
                        else -> Pair(EduSlate900, EduSlate800)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, borderCol, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isSubmitted) { viewModel.selectOption(index) },
                        color = bgCol,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val letter = listOf("A", "B", "C", "D")[index]
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EduSlate800),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letter,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = optText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSubmitted && isCorrect) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = EduEmerald500)
                            } else if (isSubmitted && isChosen && !isCorrect) {
                                Icon(Icons.Default.Cancel, contentDescription = "Incorrect", tint = EduRose500)
                            }
                        }
                    }
                }

                // Explanation Box (Shown after submission)
                if (isSubmitted) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = EduSlate900)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💡 Educational Explanation:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EduAmber500
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = q.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EduSlate200
                                )
                            }
                        }
                    }
                }

                // Submit / Next Action Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!isSubmitted) {
                        Button(
                            onClick = { viewModel.submitAnswer() },
                            enabled = selectedOption != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                        ) {
                            Text("Submit Answer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                        ) {
                            Text(
                                text = if (currentIndex + 1 < questions.size) "Next Question →" else "View Final Results 🏆",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
