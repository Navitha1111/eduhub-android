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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.AudioNarratorBar
import com.eduhub.android.ui.components.CodeSyntaxCard
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.LessonViewModel

@Composable
fun LessonDetailScreen(
    lessonId: Long,
    viewModel: LessonViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlayground: (String) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    val lesson by viewModel.lesson.collectAsState()
    val note by viewModel.note.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val speechRate by viewModel.speechRate.collectAsState()
    val isCompleted by viewModel.isCompleted.collectAsState()

    var noteText by remember(note) { mutableStateOf(note?.content ?: "") }
    var isNoteExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = lesson?.courseTitle ?: "Lesson Reader",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark() }) {
                        Icon(
                            imageVector = if (lesson?.isBookmarked == true) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (lesson?.isBookmarked == true) EduAmber500 else EduSlate400
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (lesson == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EduCyan400)
            }
        } else {
            val l = lesson!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Module Badge & Title
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = EduCyan500.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = l.moduleTitle,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = EduCyan400,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = l.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // 2. Audio Narrator (TTS Audiobook) Bar
                item {
                    AudioNarratorBar(
                        isSpeaking = isSpeaking,
                        currentRate = speechRate,
                        onPlayPause = { viewModel.playAudioNarration() },
                        onStop = { viewModel.stopAudioNarration() },
                        onCycleRate = { viewModel.cycleSpeechRate() }
                    )
                }

                // 3. Learning Objectives
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "🎯", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Learning Objective",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EduCyan400
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = l.objectives,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EduSlate200
                                )
                            }
                        }
                    }
                }

                // 4. In-Depth Lesson Content
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📖 Deep Dive Concepts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = l.contentMarkdown,
                                style = MaterialTheme.typography.bodyLarge,
                                color = EduSlate50,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                // 5. Code Syntax Block
                item {
                    CodeSyntaxCard(
                        code = l.codeExample,
                        language = l.courseSlug,
                        title = "${l.courseTitle} Practical Code",
                        onRunInPlayground = { onNavigateToPlayground(l.courseSlug) }
                    )
                }

                // 6. Expected Output Preview
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🖥️ Expected Console Output:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = EduEmerald500
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = l.expectedOutput,
                                style = MaterialTheme.typography.bodyMedium,
                                color = EduSlate200
                            )
                        }
                    }
                }

                // 7. Personal Study Notes Drawer
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EditNote, contentDescription = "Notes", tint = EduAmber500)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Personal Study Notes",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                TextButton(onClick = { isNoteExpanded = !isNoteExpanded }) {
                                    Text(
                                        text = if (isNoteExpanded) "Collapse" else "Expand Note",
                                        color = EduCyan400,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (isNoteExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { noteText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("Write your key takeaways, questions, code notes...", color = EduSlate400) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EduAmber500,
                                        unfocusedBorderColor = EduSlate700,
                                        focusedContainerColor = EduSlate800,
                                        unfocusedContainerColor = EduSlate800,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    minLines = 3
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.saveNote(noteText)
                                        Toast.makeText(context, "Note saved to your personal vault!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EduAmber500, contentColor = Color.Black),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("💾 Save Note", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // 8. Complete Lesson & Claim XP
                item {
                    Button(
                        onClick = {
                            viewModel.completeCurrentLesson()
                            Toast.makeText(context, "🎉 Lesson Completed! +50 XP Earned", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompleted) EduEmerald500 else EduCyan500,
                            contentColor = EduSlate950
                        )
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Stars,
                            contentDescription = "Complete",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCompleted) "Completed (50 XP Awarded)" else "Mark Lesson as Complete (+50 XP)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
