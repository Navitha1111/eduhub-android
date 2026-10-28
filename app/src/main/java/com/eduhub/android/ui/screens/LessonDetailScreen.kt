package com.eduhub.android.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.AudioNarratorBar
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.LessonViewModel

private val quickCodeSymbols = listOf("{", "}", "(", ")", "[", "]", "=", ":", ";", "->", "\"", "'", "<", ">", "+", "-", "*", "/", "$", "_")

data class ParsedLessonSections(
    val theory: String,
    val mechanics: String,
    val pitfalls: String,
    val bestPractices: String,
    val challenge: String
)

private fun parseLessonContent(markdown: String): ParsedLessonSections {
    var currentSection = "theory"
    val theoryLines = mutableListOf<String>()
    val mechanicsLines = mutableListOf<String>()
    val pitfallsLines = mutableListOf<String>()
    val bestPracticesLines = mutableListOf<String>()
    val challengeLines = mutableListOf<String>()

    markdown.lines().forEach { line ->
        val trimmed = line.trim()
        when {
            trimmed.startsWith("### 📖") || trimmed.contains("Theoretical Foundation") -> currentSection = "theory"
            trimmed.startsWith("### 🔍") || trimmed.contains("Code Mechanics") -> currentSection = "mechanics"
            trimmed.startsWith("### ⚠️") || trimmed.contains("Common Pitfalls") -> currentSection = "pitfalls"
            trimmed.startsWith("### 💡") || trimmed.contains("Best Practices") -> currentSection = "bestPractices"
            trimmed.startsWith("### 🧪") || trimmed.contains("Hands-On") -> currentSection = "challenge"
            else -> {
                when (currentSection) {
                    "theory" -> theoryLines.add(line)
                    "mechanics" -> mechanicsLines.add(line)
                    "pitfalls" -> pitfallsLines.add(line)
                    "bestPractices" -> bestPracticesLines.add(line)
                    "challenge" -> challengeLines.add(line)
                }
            }
        }
    }

    return ParsedLessonSections(
        theory = theoryLines.joinToString("\n").trim().ifEmpty { markdown },
        mechanics = mechanicsLines.joinToString("\n").trim(),
        pitfalls = pitfallsLines.joinToString("\n").trim(),
        bestPractices = bestPracticesLines.joinToString("\n").trim(),
        challenge = challengeLines.joinToString("\n").trim()
    )
}

@Composable
fun LessonDetailScreen(
    lessonId: Long,
    viewModel: LessonViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (Long) -> Unit,
    onNavigateToQuiz: (String) -> Unit,
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

    // Interactive Code Sandbox State
    val editableCode by viewModel.editableCode.collectAsState()
    val runOutput by viewModel.runOutput.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()

    // Course Stepper State
    val prevLessonId by viewModel.prevLessonId.collectAsState()
    val nextLessonId by viewModel.nextLessonId.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val totalLessons by viewModel.totalLessons.collectAsState()

    // Challenge & Notes State
    val showExerciseSolution by viewModel.showExerciseSolution.collectAsState()
    var noteText by remember(note) { mutableStateOf(note?.content ?: "") }
    var isNoteExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = lesson?.courseTitle ?: "Lesson Academy",
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
            val parsed = remember(l.contentMarkdown) { parseLessonContent(l.contentMarkdown) }
            val progressFraction = if (totalLessons > 0) (currentIndex + 1).toFloat() / totalLessons else 0f

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Course Stepper Progress Header
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                        Text(
                            text = "Lesson ${currentIndex + 1} of $totalLessons",
                            style = MaterialTheme.typography.labelSmall,
                            color = EduSlate400,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EduCyan500,
                        trackColor = EduSlate800
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = l.title,
                        style = MaterialTheme.typography.headlineSmall,
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
                        colors = CardDefaults.cardColors(containerColor = EduSlate900),
                        border = BorderStroke(1.dp, EduCyan500.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "🎯", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Core Learning Objective",
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

                // 4. Theoretical Foundation & Concept Deep Dive
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📖", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Concept Breakdown & Mental Model",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = parsed.theory,
                                style = MaterialTheme.typography.bodyMedium,
                                color = EduSlate50,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // 5. Code Mechanics & Syntax Explanation (if present)
                if (parsed.mechanics.isNotBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EduSlate900)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🔍", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Code Mechanics & Syntax Architecture",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EduSky400
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = parsed.mechanics,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EduSlate100,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // 6. Common Pitfalls & Gotchas Card
                if (parsed.pitfalls.isNotBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EduSlate900),
                            border = BorderStroke(1.dp, EduAmber500.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "⚠️", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Common Pitfalls & Traps to Avoid",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EduAmber400
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = parsed.pitfalls,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EduSlate100,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // 7. Best Practices & Pro Tips Card
                if (parsed.bestPractices.isNotBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EduSlate900),
                            border = BorderStroke(1.dp, EduEmerald500.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "💡", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Best Practices & Industry Conventions",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EduEmerald400
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = parsed.bestPractices,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EduSlate100,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // 8. Interactive In-Lesson Code Studio & Live Runner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CodeBg),
                        border = BorderStroke(1.dp, EduCyan500.copy(alpha = 0.3f))
                    ) {
                        Column {
                            // Studio Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(EduSlate900)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EduRose500))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EduAmber500))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(EduEmerald500))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${l.courseSlug.uppercase()} Live Code Studio",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EduCyan400,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Reset code button
                                    IconButton(
                                        onClick = {
                                            viewModel.resetCode()
                                            Toast.makeText(context, "Code restored to lesson template", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = EduSlate400, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    // Open in full playground button
                                    IconButton(
                                        onClick = { onNavigateToPlayground(l.courseSlug) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = "Full IDE", tint = EduSlate400, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            // Quick Symbols Inserter Toolbar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(EduSlate950)
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                quickCodeSymbols.forEach { sym ->
                                    Surface(
                                        onClick = { viewModel.insertSymbol(sym) },
                                        shape = RoundedCornerShape(6.dp),
                                        color = EduSlate800
                                    ) {
                                        Text(
                                            text = sym,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = EduCyan400,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            // Editable Code Editor
                            OutlinedTextField(
                                value = editableCode,
                                onValueChange = { viewModel.onCodeChanged(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 20.sp
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EduCyan500,
                                    unfocusedBorderColor = EduSlate800,
                                    focusedContainerColor = EduSlate950,
                                    unfocusedContainerColor = EduSlate950
                                ),
                                minLines = 5,
                                maxLines = 14
                            )

                            // Run Button Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Edit freely & execute",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EduSlate400
                                )

                                Button(
                                    onClick = { viewModel.runCode() },
                                    enabled = !isExecuting,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EduEmerald500, contentColor = EduSlate950),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    if (isExecuting) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = EduSlate950, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Compiling...", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    } else {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("▶ Run Code", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }

                            // Execution Console Terminal Output
                            val currentOutput = runOutput ?: l.expectedOutput
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(EduSlate950)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (runOutput != null) "🖥️ Console Output:" else "🖥️ Expected Console Output:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (runOutput != null) EduEmerald400 else EduCyan400
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentOutput,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EduSlate200,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                // 9. Hands-On Self-Study Challenge Card (if present)
                if (parsed.challenge.isNotBlank()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EduSlate900),
                            border = BorderStroke(1.dp, EduViolet500.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🧪", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Hands-On Self-Study Challenge",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EduViolet500
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = parsed.challenge,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EduSlate100,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = { viewModel.toggleExerciseSolution() },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, EduViolet500),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showExerciseSolution) Icons.Default.VisibilityOff else Icons.Default.Lightbulb,
                                        contentDescription = "Solution",
                                        tint = EduViolet500,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (showExerciseSolution) "Hide Hint & Solution" else "💡 Reveal Hint & Solution",
                                        color = EduViolet500,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                AnimatedVisibility(visible = showExerciseSolution) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EduSlate950)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Walkthrough & Solution:",
                                            fontWeight = FontWeight.Bold,
                                            color = EduCyan400,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Try modifying the parameters directly in the Live Code Studio above! Remember to test edge cases, and click '▶ Run Code' to verify your execution.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = EduSlate300
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 10. Personal Study Notes Drawer
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

                // 11. Bottom Course Stepper Navigation Bar
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900),
                        border = BorderStroke(1.dp, EduSlate800)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Complete Lesson Button
                            Button(
                                onClick = {
                                    viewModel.completeCurrentLesson()
                                    Toast.makeText(context, "🎉 Lesson Completed! +50 XP Earned", Toast.LENGTH_LONG).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCompleted) EduEmerald500 else EduCyan500,
                                    contentColor = EduSlate950
                                )
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Stars,
                                    contentDescription = "Complete",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isCompleted) "Completed (50 XP Awarded)" else "Mark Lesson as Complete (+50 XP)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Prev & Next Jumpers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Previous Lesson Button
                                OutlinedButton(
                                    onClick = {
                                        prevLessonId?.let { onNavigateToLesson(it) }
                                    },
                                    enabled = prevLessonId != null,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, if (prevLessonId != null) EduSlate700 else EduSlate800)
                                ) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Prev", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Previous", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                // Next Lesson OR Final Quiz Button
                                Button(
                                    onClick = {
                                        if (nextLessonId != null) {
                                            onNavigateToLesson(nextLessonId!!)
                                        } else {
                                            onNavigateToQuiz(l.courseSlug)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (nextLessonId != null) EduSky500 else EduAmber500,
                                        contentColor = if (nextLessonId != null) Color.White else EduSlate950
                                    )
                                ) {
                                    if (nextLessonId != null) {
                                        Text("Next Lesson", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", modifier = Modifier.size(16.dp))
                                    } else {
                                        Text("Take Quiz 🏆", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
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
