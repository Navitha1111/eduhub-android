package com.eduhub.android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun CoursesScreen(
    viewModel: CourseViewModel,
    onNavigateToCourse: (String) -> Unit
) {
    val courses by viewModel.filteredCourses.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()

    val categories = listOf(
        "All Categories",
        "General Programming",
        "Web & Full-Stack",
        "Mobile & Cross-Platform",
        "Data Science & AI",
        "Systems & Low-Level",
        "Scripting & DevOps",
        "Emerging & Specialized"
    )

    val difficulties = listOf("All Levels", "Beginner", "Intermediate", "Advanced")

    Scaffold(
        topBar = {
            CommonTopAppBar(title = "39+ Free Courses (0 to 100)")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by language, keyword, topic...", color = EduSlate400, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = EduCyan400) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = EduSlate400)
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EduCyan400,
                    unfocusedBorderColor = EduSlate700,
                    focusedContainerColor = EduSlate900,
                    unfocusedContainerColor = EduSlate900,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(cat) },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EduCyan500,
                            selectedLabelColor = EduSlate950,
                            containerColor = EduSlate900,
                            labelColor = EduSlate200
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EduCyan400 else EduSlate700
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Difficulty Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                difficulties.forEach { diff ->
                    val isSelected = selectedDifficulty == diff
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectDifficulty(diff) },
                        label = { Text(diff, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EduSky500,
                            selectedLabelColor = Color.White,
                            containerColor = EduSlate900,
                            labelColor = EduSlate400
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Course List
            if (courses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No courses found matching '$searchQuery'",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Try searching for another topic or clear filters.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EduSlate400
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(courses) { course ->
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
                                        Text(text = course.iconEmoji, fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = course.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = course.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EduCyan400
                                            )
                                        }
                                    }
                                    LevelBadge(difficulty = course.difficulty)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = course.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EduSlate400,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Progress Indicator if in progress
                                if (course.progressPercentage > 0) {
                                    LinearProgressIndicator(
                                        progress = { course.progressPercentage / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = if (course.isCompleted) EduEmerald500 else EduCyan400,
                                        trackColor = EduSlate800
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⏱️ ${course.totalHours} hrs • 📚 ${course.totalLessons} Lessons • 100% Free",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EduSlate400
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (course.isCompleted) "Completed 🏆" else "Learn 0 to 100",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (course.isCompleted) EduEmerald500 else EduCyan400,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = "Open",
                                            tint = if (course.isCompleted) EduEmerald500 else EduCyan400,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
