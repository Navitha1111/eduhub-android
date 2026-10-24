package com.eduhub.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.components.LevelBadge
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.CourseViewModel

@Composable
fun CourseDetailScreen(
    courseSlug: String,
    viewModel: CourseViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (Long) -> Unit,
    onNavigateToQuiz: (String) -> Unit,
    onNavigateToChallenge: (String) -> Unit,
    onNavigateToPlayground: (String) -> Unit
) {
    val course by viewModel.getCourse(courseSlug).collectAsState()
    val lessons by viewModel.getLessons(courseSlug).collectAsState()
    val quiz by viewModel.getQuiz(courseSlug).collectAsState()
    val challenge by viewModel.getChallenge(courseSlug).collectAsState()

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = course?.languageName ?: "Course Details",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        if (course == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EduCyan400)
            }
        } else {
            val c = course!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Course Header Card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = c.iconEmoji, fontSize = 36.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = c.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = c.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EduCyan400
                                        )
                                    }
                                }
                                LevelBadge(difficulty = c.difficulty)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = c.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = EduSlate200
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                color = EduSlate800,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "💡 Paradigm: ${c.paradigm}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EduSlate400
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Overall Progress
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Curriculum Mastery Progress",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EduSlate400
                                    )
                                    Text(
                                        text = "${c.progressPercentage}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (c.isCompleted) EduEmerald500 else EduCyan400
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { c.progressPercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (c.isCompleted) EduEmerald500 else EduCyan400,
                                    trackColor = EduSlate800
                                )
                            }
                        }
                    }
                }

                // 2. Interactive Actions (Quiz & Challenge & Playground)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Quiz Button
                        Button(
                            onClick = { onNavigateToQuiz(c.slug) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (quiz?.isPassed == true) EduEmerald500 else EduCyan500,
                                contentColor = EduSlate950
                            )
                        ) {
                            Icon(
                                imageVector = if (quiz?.isPassed == true) Icons.Default.CheckCircle else Icons.Default.Quiz,
                                contentDescription = "Quiz",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (quiz?.isPassed == true) "Quiz (${quiz?.bestScore}%)" else "Take Quiz",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Challenge Button
                        Button(
                            onClick = { onNavigateToChallenge(c.slug) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (challenge?.isSolved == true) EduEmerald500 else EduViolet500,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = if (challenge?.isSolved == true) Icons.Default.CheckCircle else Icons.Default.Code,
                                contentDescription = "Challenge",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (challenge?.isSolved == true) "Solved 100XP" else "Challenge",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // 3. 0-to-Advanced 4-Tier Modules & Lessons
                item {
                    Text(
                        text = "🗺️ 4-Tier Progressive Roadmap (0 to 100%)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                val groupedLessons = lessons.groupBy { it.moduleTitle }

                groupedLessons.forEach { (moduleTitle, moduleLessons) ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EduSlate900)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = moduleTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EduCyan400
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                moduleLessons.forEach { lesson ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onNavigateToLesson(lesson.id) }
                                            .padding(vertical = 8.dp, horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(if (lesson.isCompleted) EduEmerald500 else EduSlate800),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (lesson.isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Done",
                                                    tint = EduSlate950,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = "${lesson.lessonOrder}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EduSlate400
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = lesson.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = lesson.objectives,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EduSlate400,
                                                maxLines = 1
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = "Start",
                                            tint = if (lesson.isCompleted) EduEmerald500 else EduCyan400,
                                            modifier = Modifier.size(24.dp)
                                        )
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
