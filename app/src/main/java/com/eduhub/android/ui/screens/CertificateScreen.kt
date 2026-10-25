package com.eduhub.android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.ui.components.CertificateCard
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import com.eduhub.android.ui.viewmodels.CertificateViewModel
import com.eduhub.android.utils.ShareUtil

@Composable
fun CertificateScreen(
    viewModel: CertificateViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val certificates by viewModel.certificates.collectAsState()
    val verificationResult by viewModel.verificationResult.collectAsState()
    val isVerifying by viewModel.isVerifying.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val claimMessage by viewModel.claimMessage.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0 = My Certificates, 1 = Verify Credential
    var searchId by remember { mutableStateOf("") }

    // Auto-sync certificates if list is currently empty
    LaunchedEffect(Unit) {
        if (certificates.isEmpty()) {
            viewModel.syncCertificates()
        }
    }

    LaunchedEffect(claimMessage) {
        claimMessage?.let { msg ->
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearClaimMessage()
        }
    }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "Verifiable Certificates",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = EduSlate950
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = EduSlate900,
                contentColor = EduCyan400,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, EduSlate800, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "My Certificates (${certificates.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) EduCyan300 else EduSlate400
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Verify Credential",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) EduCyan300 else EduSlate400
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // MY CERTIFICATES LIST
                if (certificates.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🎓", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Cloud Account Certificates",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Did you complete coursework on your laptop or web? Tap below to synchronize your account and restore your official diplomas to this device.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EduSlate400,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.syncCertificates() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EduCyan500),
                            enabled = !isSyncing
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(color = EduSlate950, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Syncing Account Credentials...", color = EduSlate950, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, tint = EduSlate950, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Restore Account Certificates", color = EduSlate950, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Quick-Restore Available Track Cards
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = EduSlate900),
                            border = BorderStroke(1.dp, EduSlate800)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ Quick Coursework Import",
                                    color = EduCyan300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Select any completed track to immediately issue its verified certificate to your wallet:",
                                    color = EduSlate400,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                val popularTracks = listOf(
                                    "bash" to "Bash & Linux Shell Scripting (0 to 100)",
                                    "css" to "CSS3 Modern Styling & Flexbox (0 to 100)",
                                    "docker" to "Docker Containers & DevOps (0 to 100)",
                                    "golang" to "Go Backend Systems & Concurrency (0 to 100)",
                                    "python" to "Python Modern Programming & Automation (0 to 100)"
                                )

                                popularTracks.forEach { (slug, title) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = title,
                                            color = EduSlate200,
                                            fontSize = 11.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.claimCertificateForCourse(slug) },
                                            modifier = Modifier.height(30.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, EduCyan400),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                        ) {
                                            Text("Import", fontSize = 11.sp, color = EduCyan300, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🏆 Earned Industry Mastery Credentials (${certificates.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                IconButton(
                                    onClick = { viewModel.syncCertificates() },
                                    enabled = !isSyncing
                                ) {
                                    if (isSyncing) {
                                        CircularProgressIndicator(color = EduCyan400, modifier = Modifier.size(18.dp))
                                    } else {
                                        Icon(Icons.Default.Refresh, contentDescription = "Sync Cloud", tint = EduCyan400)
                                    }
                                }
                            }
                        }

                        items(certificates) { cert ->
                            CertificateCard(
                                certificate = cert,
                                onShare = { ShareUtil.shareCertificate(context, cert) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            } else {
                // VERIFICATION TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = EduSlate900),
                            border = BorderStroke(1.dp, EduSlate800)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EduCyan400)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Official Credential Verification",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Enter any EDUHUB Certificate ID or SHA-256 digital signature to verify its authenticity and issued credentials.",
                                    color = EduSlate400,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = searchId,
                                    onValueChange = { searchId = it },
                                    label = { Text("Certificate ID or Hash", color = EduSlate400) },
                                    placeholder = { Text("e.g. EDUHUB-2026-PYTHON-101ABCD", color = EduSlate500) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EduCyan400) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = EduCyan400,
                                        unfocusedBorderColor = EduSlate700
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { viewModel.verifyCertificate(searchId.trim()) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EduCyan500),
                                    enabled = searchId.isNotBlank() && !isVerifying
                                ) {
                                    if (isVerifying) {
                                        CircularProgressIndicator(color = EduSlate950, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Verify Authenticity", color = EduSlate950, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Verification Output Result
                    verificationResult?.let { res ->
                        item {
                            if (res.isValid) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = EduSlate900),
                                    border = BorderStroke(1.5.dp, EduEmerald500)
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(EduEmerald500.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EduEmerald400)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = "VERIFIED OFFICIAL CREDENTIAL",
                                                    color = EduEmerald400,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    letterSpacing = 1.sp
                                                )
                                                Text(
                                                    text = res.message,
                                                    color = EduSlate300,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))
                                        HorizontalDivider(color = EduSlate800)
                                        Spacer(modifier = Modifier.height(14.dp))

                                        DetailRow("Student Name", res.studentName, isBold = true)
                                        DetailRow("Course Title", res.courseTitle)
                                        DetailRow("Issued Date", res.issueDate)
                                        DetailRow("Honors Grade", res.grade)
                                        DetailRow("Certificate ID", res.certificateId, isMonospace = true)
                                        if (res.sha256Signature.isNotBlank()) {
                                            DetailRow("SHA-256 Hash", res.sha256Signature, isMonospace = true)
                                        }

                                        if (res.aiCommendation.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Surface(
                                                color = EduSlate950,
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(1.dp, EduSlate800),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "“${res.aiCommendation}”",
                                                    color = EduCyan200,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.padding(12.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Button(
                                            onClick = {
                                                val slug = when {
                                                    res.courseTitle.contains("Bash", ignoreCase = true) -> "bash"
                                                    res.courseTitle.contains("CSS", ignoreCase = true) -> "css"
                                                    res.courseTitle.contains("Docker", ignoreCase = true) -> "docker"
                                                    res.courseTitle.contains("Go", ignoreCase = true) -> "golang"
                                                    res.courseTitle.contains("Python", ignoreCase = true) -> "python"
                                                    res.courseTitle.contains("JavaScript", ignoreCase = true) -> "javascript"
                                                    res.courseTitle.contains("Java", ignoreCase = true) -> "java"
                                                    res.courseTitle.contains("Rust", ignoreCase = true) -> "rust"
                                                    res.courseTitle.contains("Kotlin", ignoreCase = true) -> "kotlin"
                                                    else -> "bash"
                                                }
                                                viewModel.claimCertificateForCourse(slug)
                                                selectedTab = 0
                                            },
                                            modifier = Modifier.fillMaxWidth().height(44.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = EduEmerald500)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EduSlate950, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Save to My Certificates", color = EduSlate950, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = EduSlate900),
                                    border = BorderStroke(1.5.dp, EduRose500)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(18.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EduRose400, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = "Unverified Credential",
                                                color = EduRose400,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = res.message,
                                                color = EduSlate400,
                                                fontSize = 12.sp
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
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = EduSlate400, fontSize = 13.sp)
        Text(
            text = value,
            color = Color.White,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            fontSize = if (isMonospace) 12.sp else 13.sp
        )
    }
}
