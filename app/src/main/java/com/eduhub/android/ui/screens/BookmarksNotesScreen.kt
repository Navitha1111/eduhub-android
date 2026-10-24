package com.eduhub.android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.BookmarksNotesViewModel

@Composable
fun BookmarksNotesScreen(
    viewModel: BookmarksNotesViewModel,
    onNavigateToLesson: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val notes by viewModel.notes.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Bookmarks, 1: Notes

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Saved Lessons & Notes",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = EduSlate900,
                contentColor = EduCyan400
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("🔖 Bookmarked Lessons (${bookmarks.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("📝 Study Notes (${notes.size})", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Bookmarked Lessons Tab
                if (bookmarks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔖", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No Bookmarked Lessons", style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text("Tap the bookmark icon on any lesson to save it here.", color = EduSlate400, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(bookmarks) { b ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToLesson(b.lessonId) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = EduSlate900)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = b.courseTitle, style = MaterialTheme.typography.labelSmall, color = EduCyan400)
                                        Text(text = b.lessonTitle, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(text = b.moduleTitle, style = MaterialTheme.typography.bodySmall, color = EduSlate400)
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Open", tint = EduCyan400)
                                }
                            }
                        }
                    }
                }
            } else {
                // Notes Tab
                if (notes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📝", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No Personal Study Notes", style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text("Add notes during your lessons to access them in your vault.", color = EduSlate400, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notes) { n ->
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
                                        Column {
                                            Text(text = n.courseTitle, style = MaterialTheme.typography.labelSmall, color = EduAmber500)
                                            Text(text = n.lessonTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        IconButton(onClick = { viewModel.deleteNote(n) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EduRose500, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = EduSlate800,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = n.content,
                                            modifier = Modifier.padding(10.dp),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = EduSlate200
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
