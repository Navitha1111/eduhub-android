package com.eduhub.android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToCertificates: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToAuth: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    val progress by viewModel.userProgress.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember(progress) { mutableStateOf(progress?.studentName ?: "") }

    val xp = progress?.totalXp ?: 150
    val streak = progress?.currentStreak ?: 1
    val lessonsDone = progress?.completedLessonsCount ?: 0
    val quizzesDone = progress?.completedQuizzesCount ?: 0
    val challengesDone = progress?.completedChallengesCount ?: 0
    val isDark = progress?.isDarkMode ?: true
    val isLoggedIn = progress?.isLoggedIn ?: false

    val levelNum = (xp / 100) + 1
    val rankTitle = when {
        xp >= 2000 -> "👑 Polyglot Grandmaster"
        xp >= 1000 -> "🔥 Senior Architecture Specialist"
        xp >= 500  -> "⚡ Competent Full-Stack Developer"
        else       -> "🌱 Active Coding Explorer"
    }

    LaunchedEffect(syncMessage) {
        syncMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearSyncMessage()
        }
    }

    Scaffold(
        topBar = {
            CommonTopAppBar(title = "Learner Profile & Stats")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 0. VPS Cloud Account & Registration Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                if (isLoggedIn) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EduCyan500.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(EduEmerald400)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Connected to VPS (162.35.9.208:8080)",
                                    color = EduEmerald300,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EduCyan500.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = progress?.role ?: "STUDENT",
                                        color = EduCyan300,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = progress?.studentName ?: "Student",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "@${progress?.username ?: ""} • ${progress?.email ?: ""}",
                                color = EduSlate400,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.syncWithVps() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EduCyan400),
                                    enabled = !isSyncing
                                ) {
                                    if (isSyncing) {
                                        CircularProgressIndicator(color = EduCyan400, modifier = Modifier.size(16.dp))
                                    } else {
                                        Icon(Icons.Default.Sync, contentDescription = null, tint = EduCyan300, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sync Now", color = EduCyan300, fontSize = 13.sp)
                                    }
                                }

                                TextButton(
                                    onClick = {
                                        viewModel.logout()
                                        onSignOut()
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Sign Out", color = EduRose400, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onNavigateToAuth() },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EduCyan950.copy(alpha = 0.5f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EduCyan400.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(EduCyan500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, tint = EduCyan300)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Connect to EDUHUB VPS Cloud",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Sign in or register free account to sync certificates & progress with VPS.",
                                    color = EduSlate300,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EduCyan400)
                        }
                    }
                }
            }

            // 1. Profile Header Card
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EduSlate900)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(EduCyan500.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👨‍💻", fontSize = 36.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = progress?.studentName ?: "Student",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    tempName = progress?.studentName ?: ""
                                    showEditNameDialog = true
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = EduCyan400, modifier = Modifier.size(16.dp))
                            }
                        }

                        Text(
                            text = "Level $levelNum • $rankTitle",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = EduCyan400
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid (4 Cards)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox("Total XP", "$xp", EduCyan400, Modifier.weight(1f))
                            StatBox("Streak", "$streak d", EduAmber500, Modifier.weight(1f))
                            StatBox("Lessons", "$lessonsDone", EduEmerald500, Modifier.weight(1f))
                            StatBox("Quizzes", "$quizzesDone", EduViolet500, Modifier.weight(1f))
                        }
                    }
                }
            }

            // 2. Achievements Shelf
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EduSlate900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🏅 Learning Milestones & Badges",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BadgeItem("🌱 First Step", "Started learning", true)
                            BadgeItem("🔥 Streak 7", "Consistent study", streak >= 7)
                            BadgeItem("🧠 Quiz Master", "Scored 90%+", quizzesDone >= 1)
                            BadgeItem("🏆 Certified", "Completed course", lessonsDone >= 8)
                        }
                    }
                }
            }

            // 3. Quick Navigation Hub
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EduSlate900)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        ListItem(
                            headlineContent = { Text("Verifiable Certificates", color = Color.White, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("View & export completed certificates", color = EduSlate400) },
                            leadingContent = { Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = EduAmber500) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EduSlate400) },
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable { onNavigateToCertificates() },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                        HorizontalDivider(color = EduSlate800)
                        ListItem(
                            headlineContent = { Text("Saved Lessons & Notes", color = Color.White, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Manage bookmarked topics and personal code notes", color = EduSlate400) },
                            leadingContent = { Icon(Icons.Default.Bookmark, contentDescription = null, tint = EduCyan400) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EduSlate400) },
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable { onNavigateToBookmarks() },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                        HorizontalDivider(color = EduSlate800)
                        ListItem(
                            headlineContent = { Text("100% Free Forever Guarantee", color = Color.White, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Zero paywalls, zero ads, all 39+ language tracks included", color = EduSlate400) },
                            leadingContent = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EduEmerald500) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Student Name") },
            text = {
                Column {
                    Text("This name will appear on all your official EDUHUB Certificates.", fontSize = 13.sp, color = EduSlate400)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateName(tempName)
                        showEditNameDialog = false
                        Toast.makeText(context, "Student name updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) { Text("Cancel") }
            },
            containerColor = EduSlate900
        )
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = EduSlate800,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = EduSlate400, fontSize = 10.sp)
        }
    }
}

@Composable
fun BadgeItem(title: String, desc: String, isUnlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) EduCyan500.copy(alpha = 0.2f) else EduSlate800),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title.split(" ").firstOrNull() ?: "🏅",
                fontSize = 20.sp,
                color = if (isUnlocked) Color.White else EduSlate600
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title.substringAfter(" "),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isUnlocked) Color.White else EduSlate600,
            fontSize = 10.sp
        )
    }
}
