package com.eduhub.android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.AdminCourseCreatorViewModel

@Composable
fun AdminCourseCreatorScreen(
    viewModel: AdminCourseCreatorViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var techName by remember { mutableStateOf("") }
    val statusMsg by viewModel.statusMessage.collectAsState()

    LaunchedEffect(statusMsg) {
        if (statusMsg != null) {
            Toast.makeText(context, statusMsg, Toast.LENGTH_LONG).show()
            viewModel.clearStatus()
        }
    }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "⚡ 1-Click Course Generator",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = EduSlate900)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Generator", tint = EduRose500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1-Click Curriculum Auto-Generator",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Enter any programming language, library, or framework name (e.g. Mojo, Solidity, Zig, Elixir, GraphQL, Tailwind) to automatically construct a complete 4-tier progressive curriculum with lessons, quiz, and coding challenge in your local database!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EduSlate200
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = techName,
                            onValueChange = { techName = it },
                            label = { Text("Technology / Language Name") },
                            placeholder = { Text("e.g. Zig, Mojo, Fortran, Next.js") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EduCyan400,
                                unfocusedBorderColor = EduSlate700,
                                focusedContainerColor = EduSlate800,
                                unfocusedContainerColor = EduSlate800,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (techName.isNotBlank()) {
                                    viewModel.autoGenerateCourse(techName.trim())
                                    techName = ""
                                } else {
                                    Toast.makeText(context, "Please enter a technology name", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Generate")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("⚡ Generate Full 4-Tier Course & Publish", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EduSlate900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💡 What gets auto-generated?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EduAmber500
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Level 0: Syntax, Types & Memory Semantics\n• Level 1: Control Flow, Loops & Conditionals\n• Level 2: Collections, Functions & Modularity\n• Level 3: OOP, Error Safety & Capstone Architecture\n• Interactive 5-question mastery quiz with explanations\n• Rubric-checked coding challenge & starter template\n• Automatic certificate generation eligibility",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EduSlate200,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
