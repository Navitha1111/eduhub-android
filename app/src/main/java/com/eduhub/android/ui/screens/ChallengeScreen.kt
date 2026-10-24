package com.eduhub.android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.ChallengeViewModel

@Composable
fun ChallengeScreen(
    courseSlug: String,
    viewModel: ChallengeViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(courseSlug) {
        viewModel.loadChallenge(courseSlug)
    }

    val challenge by viewModel.challenge.collectAsState()
    val userCode by viewModel.userCode.collectAsState()
    val result by viewModel.executionResult.collectAsState()
    val showHint by viewModel.showHint.collectAsState()
    val isSolved by viewModel.isSolved.collectAsState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = challenge?.languageName?.let { "$it Challenge" } ?: "Coding Challenge",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        if (challenge == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EduCyan400)
            }
        } else {
            val ch = challenge!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Challenge Title & XP Reward
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ch.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Surface(
                                    color = if (isSolved) EduEmerald500 else EduAmber500.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isSolved) "✅ Solved (+${ch.earnedXp} XP)" else "⚡ ${ch.earnedXp} XP",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSolved) EduSlate950 else EduAmber500
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = ch.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = EduSlate200
                            )
                        }
                    }
                }

                // Hint Drawer
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = "Hint", tint = EduAmber500)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Need a Hint?",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                TextButton(onClick = { viewModel.toggleHint() }) {
                                    Text(if (showHint) "Hide Hint" else "Reveal Hint", color = EduCyan400)
                                }
                            }
                            if (showHint) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ch.hint,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EduSlate200
                                )
                            }
                        }
                    }
                }

                // Interactive Code Editor
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CodeBg)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(EduSlate900)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Solution Code Editor (${ch.languageName})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EduSlate400,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(onClick = { viewModel.resetStarter() }) {
                                    Text("Reset Starter", color = EduCyan400, fontSize = 11.sp)
                                }
                            }

                            OutlinedTextField(
                                value = userCode,
                                onValueChange = { viewModel.updateCode(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                textStyle = LocalTextStyle.current.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = EduSlate50
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = CodeBg,
                                    unfocusedContainerColor = CodeBg
                                ),
                                minLines = 8
                            )
                        }
                    }
                }

                // Action Button: Run & Verify
                item {
                    Button(
                        onClick = {
                            viewModel.runAndVerifyCode()
                            Toast.makeText(context, "Evaluating code with Rubric Verification...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⚡ Run & Verify Challenge", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                // Test / Rubric Result Terminal Output
                if (result != null) {
                    item {
                        val res = result!!
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (res.isSuccess) EduSlate900 else EduSlate900
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (res.isSuccess) "🎉 Challenge Solved!" else "⚠️ Test Criteria Feedback",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.isSuccess) EduEmerald500 else EduAmber500
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = res.output,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = EduSlate200,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
