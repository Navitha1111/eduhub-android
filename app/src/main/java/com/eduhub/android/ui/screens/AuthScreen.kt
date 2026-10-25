package com.eduhub.android.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduhub.android.data.repository.EduHubRepository
import com.eduhub.android.ui.components.CommonTopAppBar
import com.eduhub.android.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    repository: EduHubRepository,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0 = Register, 1 = Login

    // Register Form Fields
    var regFullName by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }

    // Login Form Fields
    var loginIdentity by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = if (selectedTab == 0) "Create Student Account" else "Sign In to Continue",
                canNavigateBack = canNavigateBack,
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = EduSlate950
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Clean Branding & Auth Notice Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EduSlate900),
                border = androidx.compose.foundation.BorderStroke(1.dp, EduCyan400.copy(alpha = 0.4f))
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
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = EduCyan300,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Student Login Required",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Sign in or create your free account to unlock 39+ programming tracks and sync your learning progress.",
                            color = EduSlate300,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Navigation Tab Selector: Register vs Login
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = EduSlate900,
                contentColor = EduCyan400,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, EduSlate800, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        errorMessage = null
                    },
                    text = {
                        Text(
                            "New Student Register",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 0) EduCyan300 else EduSlate400
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        errorMessage = null
                    },
                    text = {
                        Text(
                            "Sign In",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == 1) EduCyan300 else EduSlate400
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error Banner
            errorMessage?.let { err ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EduRose500.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EduRose500.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = "Error", tint = EduRose400)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = err, color = EduRose300, fontSize = 13.sp)
                    }
                }
            }

            if (selectedTab == 0) {
                // REGISTRATION FORM
                OutlinedTextField(
                    value = regFullName,
                    onValueChange = { regFullName = it },
                    label = { Text("Full Name (as on Certificate)", color = EduSlate400) },
                    placeholder = { Text("Alex Mercer", color = EduSlate500) },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = EduSlate400) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EduCyan400,
                        unfocusedBorderColor = EduSlate800
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regUsername,
                    onValueChange = { regUsername = it.lowercase().trim() },
                    label = { Text("Username", color = EduSlate400) },
                    placeholder = { Text("alex_mercer", color = EduSlate500) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EduSlate400) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EduCyan400,
                        unfocusedBorderColor = EduSlate800
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regEmail,
                    onValueChange = { regEmail = it.trim() },
                    label = { Text("Email Address", color = EduSlate400) },
                    placeholder = { Text("alex@example.com", color = EduSlate500) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EduSlate400) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EduCyan400,
                        unfocusedBorderColor = EduSlate800
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regPassword,
                    onValueChange = { regPassword = it },
                    label = { Text("Password (min 8 characters)", color = EduSlate400) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EduSlate400) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = EduSlate400
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EduCyan400,
                        unfocusedBorderColor = EduSlate800
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regConfirmPassword,
                    onValueChange = { regConfirmPassword = it },
                    label = { Text("Confirm Password", color = EduSlate400) },
                    leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = EduSlate400) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EduCyan400,
                        unfocusedBorderColor = EduSlate800
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (regFullName.isBlank() || regUsername.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
                            errorMessage = "Please fill in all registration fields."
                            return@Button
                        }
                        if (regPassword.length < 8) {
                            errorMessage = "Password must be at least 8 characters long."
                            return@Button
                        }
                        if (regPassword != regConfirmPassword) {
                            errorMessage = "Passwords do not match."
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            val res = repository.registerOnline(
                                fullName = regFullName.trim(),
                                username = regUsername.trim(),
                                email = regEmail.trim(),
                                password = regPassword
                            )
                            isLoading = false
                            if (res.ok) {
                                Toast.makeText(context, "Welcome, ${res.fullName}! Account created successfully.", Toast.LENGTH_LONG).show()
                                onAuthSuccess()
                            } else {
                                errorMessage = res.errorMessage
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduCyan500),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = EduSlate950)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Student Account", color = EduSlate950, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else {
                // LOGIN FORM
                OutlinedTextField(
                    value = loginIdentity,
                    onValueChange = { loginIdentity = it },
                    label = { Text("Username or Email", color = EduSlate400) },
                    placeholder = { Text("username or name@example.com", color = EduSlate500) },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = EduSlate400) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EduCyan400,
                        unfocusedBorderColor = EduSlate800
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = loginPassword,
                    onValueChange = { loginPassword = it },
                    label = { Text("Password", color = EduSlate400) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EduSlate400) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = EduSlate400
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = EduCyan400,
                        unfocusedBorderColor = EduSlate800
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (loginIdentity.isBlank() || loginPassword.isBlank()) {
                            errorMessage = "Please enter both username/email and password."
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            val res = repository.loginOnline(loginIdentity.trim(), loginPassword)
                            isLoading = false
                            if (res.ok) {
                                Toast.makeText(context, "Signed in as ${res.fullName}", Toast.LENGTH_LONG).show()
                                onAuthSuccess()
                            } else {
                                errorMessage = res.errorMessage
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduCyan500),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Login, contentDescription = null, tint = EduSlate950)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In to Account", color = EduSlate950, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Information Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EduSlate900.copy(alpha = 0.6f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EduSlate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Why connect to EDUHUB Cloud?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Seamless sync: Your completed courses, XP points, and streak automatically sync to your cloud profile.\n" +
                               "• Verifiable Certificates: Credentials carry a cryptographically signed SHA-256 seal issued in your name.\n" +
                               "• Cross-Platform: Use the same account across both EDUHUB Desktop and this Android app.",
                        color = EduSlate400,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
