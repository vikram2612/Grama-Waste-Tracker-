package com.grama.wastetracker.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.grama.wastetracker.R
import com.grama.wastetracker.data.model.User
import com.grama.wastetracker.ui.theme.*
import com.grama.wastetracker.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var tractorNumber by remember { mutableStateOf("") } // For Tractor registration
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 for Citizen, 1 for Tractor

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) onRegisterSuccess()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (selectedTab == 0)
                            listOf(GradientGreenStart, GradientGreenEnd, BackgroundLight)
                        else
                            listOf(BluePrimary, BluePrimaryDark, BackgroundLight),
                        startY = 0f, endY = 1400f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App Icon
            Surface(
                modifier = Modifier.size(70.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.2f),
                border = BorderStroke(2.dp, Color.White.copy(alpha = 0.4f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (selectedTab == 0) Icons.Filled.Eco else Icons.Filled.LocalShipping,
                        null, Modifier.size(40.dp), tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                if (selectedTab == 0) stringResource(R.string.app_name) else "Driver Registration",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White, fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(4.dp)
            ) {
                TabButton(
                    modifier = Modifier.weight(1f),
                    selected = selectedTab == 0,
                    text = "Citizen",
                    onClick = { selectedTab = 0 }
                )
                TabButton(
                    modifier = Modifier.weight(1f),
                    selected = selectedTab == 1,
                    text = "Driver",
                    onClick = { selectedTab = 1 }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (selectedTab == 0) "Create Account" else "Tractor Onboarding",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (selectedTab == 0) GreenPrimaryDark else BluePrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = fullName, onValueChange = { fullName = it },
                        label = { Text(if (selectedTab == 0) stringResource(R.string.full_name) else "Driver Full Name") },
                        leadingIcon = { Icon(Icons.Outlined.Person, null, tint = if (selectedTab == 0) GreenPrimary else BluePrimary) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (selectedTab == 0) GreenPrimary else BluePrimary,
                            cursorColor = if (selectedTab == 0) GreenPrimary else BluePrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text(stringResource(R.string.email)) },
                        leadingIcon = { Icon(Icons.Outlined.Email, null, tint = if (selectedTab == 0) GreenPrimary else BluePrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (selectedTab == 0) GreenPrimary else BluePrimary,
                            cursorColor = if (selectedTab == 0) GreenPrimary else BluePrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        label = { Text(stringResource(R.string.phone_number)) },
                        leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = if (selectedTab == 0) GreenPrimary else BluePrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (selectedTab == 0) GreenPrimary else BluePrimary,
                            cursorColor = if (selectedTab == 0) GreenPrimary else BluePrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTab == 1) {
                        OutlinedTextField(
                            value = tractorNumber, onValueChange = { tractorNumber = it },
                            label = { Text("Tractor Number (e.g. KA-01-1234)") },
                            leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = BluePrimary) },
                            singleLine = true, modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = BluePrimary,
                                cursorColor = BluePrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    OutlinedTextField(
                        value = village, onValueChange = { village = it },
                        label = { Text(stringResource(R.string.village_name)) },
                        leadingIcon = { Icon(Icons.Outlined.LocationCity, null, tint = if (selectedTab == 0) GreenPrimary else BluePrimary) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (selectedTab == 0) GreenPrimary else BluePrimary,
                            cursorColor = if (selectedTab == 0) GreenPrimary else BluePrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text(stringResource(R.string.password)) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = if (selectedTab == 0) GreenPrimary else BluePrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = if (selectedTab == 0) GreenPrimary else BluePrimary,
                            cursorColor = if (selectedTab == 0) GreenPrimary else BluePrimary
                        )
                    )

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = RedError.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(uiState.error!!, color = RedError, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val role = if (selectedTab == 0) User.ROLE_CITIZEN else User.ROLE_TRACTOR
                            // We need to pass the extra field for tractor in a production app,
                            // but for now we'll use existing register call and handle role.
                            viewModel.register(fullName, email, password, phone, village, role)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 0) GreenPrimary else BluePrimary
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                stringResource(R.string.register),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            stringResource(R.string.already_have_account),
                            color = if (selectedTab == 0) GreenPrimary else BluePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
