package com.eduhub.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.components.DailyStreakBanner
import com.eduhub.android.ui.components.LevelBadge
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToPlayground: () -> Unit,
    onNavigateToFlashcards: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToCertificates: () -> Unit,
    onNavigateToCreator: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToCheatSheet: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {}
) {
    val userProgress by viewModel.userProgress.collectAsState()
    val inProgressCourses by viewModel.inProgressCourses.collectAsState()
    val allCourses by viewModel.allCourses.collectAsState()

    val streak = userProgress?.currentStreak ?: 1
    val xp = userProgress?.totalXp ?: 150
    val studentName = userProgress?.studentName ?: "Learner"

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "EDUHUB Mobile",
                streakDays = streak,
                totalXp = xp
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Welcome Greeting Header
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    Text(
                        text = if (userProgress?.isLoggedIn == true) "Welcome back, $studentName! 👋" else "Welcome to EDUHUB! 🚀",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (userProgress?.isLoggedIn == true)
                            "VPS Cloud Sync Active • 39+ Tracks"
                        else
                            "Sign in with your VPS account to access full 0-to-advanced courses",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EduSlate400
                    )
                }
            }

            // 1.5 Mandatory Login Banner (if not authenticated)
            if (userProgress?.isLoggedIn != true) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToAuth() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EduCyan950.copy(alpha = 0.6f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EduCyan400)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = EduCyan300,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sign In to Access All Courses",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Courses and certificates require a registered student account. Tap here to sign in or create an account on the VPS server.",
                                    color = EduSlate300,
                                    fontSize = 12.sp
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EduCyan400)
                        }
                    }
                }
            }

            // 2. Daily Streak Banner with Smart Daily Plan
            item {
                DailyStreakBanner(
                    streakDays = streak,
                    totalXp = xp,
                    levelTitle = when {
                        xp >= 2000 -> "👑 Master Architect"
                        xp >= 1000 -> "🔥 Senior Specialist"
                        xp >= 500  -> "⚡ Competent Developer"
                        else       -> "🌱 Beginner Explorer"
                    },
                    onDailyActionClick = {
                        val next = inProgressCourses.firstOrNull() ?: allCourses.firstOrNull()
                        if (next != null) onNavigateToCourse(next.slug) else onNavigateToCourses()
                    }
                )
            }

            // 3. Quick Action Hub (Horizontal Scroll)
            item {
                Column {
                    Text(
                        text = "🚀 Quick Learning Tools",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickToolPill("Playground", Icons.Default.Code, EduCyan400, onNavigateToPlayground)
                        QuickToolPill("Flashcards", Icons.Default.Style, EduAmber500, onNavigateToFlashcards)
                        QuickToolPill("Cheat Sheet", Icons.Default.Description, EduSky400, onNavigateToCheatSheet)
                        QuickToolPill("Certificates", Icons.Default.EmojiEvents, EduEmerald500, onNavigateToCertificates)
                        QuickToolPill("Saved Notes", Icons.Default.Bookmark, EduViolet500, onNavigateToBookmarks)
                        QuickToolPill("Auto Creator", Icons.Default.AutoAwesome, EduRose500, onNavigateToCreator)
                    }
                }
            }

            // 4. In-Progress Courses (if any)
            if (inProgressCourses.isNotEmpty()) {
                item {
                    Text(
                        text = "📖 Continue Your 0-to-Advanced Journey",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                items(inProgressCourses) { course ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToCourse(course.slug) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = course.iconEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = course.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { course.progressPercentage / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = EduCyan400,
                                    trackColor = EduSlate800
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${course.progressPercentage}% Completed • ${course.completedLessons}/${course.totalLessons} Lessons",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EduSlate400
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open",
                                tint = EduCyan400
                            )
                        }
                    }
                }
            }

            // 5. Featured 0-to-Advanced Tracks
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌟 Popular Mastery Tracks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    TextButton(onClick = onNavigateToCourses) {
                        Text(text = "View All 39+ →", color = EduCyan400, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(allCourses.take(5)) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCourse(course.slug) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EduSlate900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = course.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = course.languageName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            LevelBadge(difficulty = course.difficulty)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = course.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = EduSlate400,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⏱️ ${course.totalHours} hrs • 📚 ${course.totalLessons} Lessons • 100% Free",
                                style = MaterialTheme.typography.labelSmall,
                                color = EduCyan400,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Start Track →",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
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

@Composable
fun QuickToolPill(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = EduSlate900,
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
