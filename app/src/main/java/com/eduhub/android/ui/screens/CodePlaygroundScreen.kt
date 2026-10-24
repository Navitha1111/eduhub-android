package com.eduhub.android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import com.eduhub.android.data.seed.LanguageCatalogData
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.PlaygroundViewModel
import com.eduhub.android.utils.ShareUtil

@Composable
fun CodePlaygroundScreen(
    initialLanguage: String = "python",
    viewModel: PlaygroundViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(initialLanguage) {
        if (initialLanguage.isNotBlank()) {
            viewModel.setLanguage(initialLanguage)
        }
    }

    val selectedLang by viewModel.selectedLanguage.collectAsState()
    val codeBuffer by viewModel.codeBuffer.collectAsState()
    val terminalOutput by viewModel.terminalOutput.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var snippetTitle by remember { mutableStateOf("") }

    val quickSymbols = listOf("{", "}", "(", ")", "[", "]", ";", ":", "=", "->", "==", "!=", "\"", "'", "$", "<", ">", "+", "-", "*", "/")

    Scaffold(
        topBar = {
            CommonTopAppBar(title = "Multi-Language Playground")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Language Selector Chips
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageCatalogData.languages.forEach { spec ->
                        val isSelected = selectedLang == spec.slug
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setLanguage(spec.slug) },
                            label = { Text("${spec.iconEmoji} ${spec.langName}", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EduCyan500,
                                selectedLabelColor = EduSlate950,
                                containerColor = EduSlate900,
                                labelColor = EduSlate200
                            )
                        )
                    }
                }
            }

            // 2. Quick Symbol Inserter Toolbar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickSymbols.forEach { sym ->
                        Surface(
                            onClick = { viewModel.insertSymbol(sym) },
                            shape = RoundedCornerShape(8.dp),
                            color = EduSlate800
                        ) {
                            Text(
                                text = sym,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = EduCyan400,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 3. Code Editor Box
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
                                text = "${selectedLang.uppercase()} Studio Editor",
                                style = MaterialTheme.typography.labelSmall,
                                color = EduCyan400,
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        snippetTitle = "$selectedLang Snippet"
                                        showSaveDialog = true
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.BookmarkAdd, contentDescription = "Save", tint = EduSlate400, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        ShareUtil.shareSnippet(context, "$selectedLang Code", selectedLang, codeBuffer)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = EduSlate400, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        OutlinedTextField(
                            value = codeBuffer,
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
                            minLines = 10
                        )
                    }
                }
            }

            // 4. Action Buttons (Run & Clear)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.runCode() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Run")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Run Simulator", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.clearTerminal() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Clear Console", color = EduSlate400)
                    }
                }
            }

            // 5. Terminal Console Output
            if (terminalOutput != null) {
                item {
                    val out = terminalOutput!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EduSlate900)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🖥️ Console Standard Output",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (out.isSuccess) EduEmerald500 else EduRose500
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = out.output,
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

    // Save Snippet Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Code Snippet") },
            text = {
                Column {
                    Text("Enter a title for this snippet to access it anytime from Saved Notes.", fontSize = 13.sp, color = EduSlate400)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = snippetTitle,
                        onValueChange = { snippetTitle = it },
                        label = { Text("Snippet Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveAsSnippet(snippetTitle)
                        showSaveDialog = false
                        Toast.makeText(context, "Snippet saved to library!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EduCyan500, contentColor = EduSlate950)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = EduSlate900
        )
    }
}
