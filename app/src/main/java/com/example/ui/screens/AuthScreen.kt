package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AgrivaLogo
import com.example.ui.theme.AgrivaGreen
import com.example.ui.theme.AgrivaDarkGreen
import com.example.ui.theme.AgrivaYellow
import com.example.ui.viewmodel.AgrivaViewModel

@Composable
fun AuthScreen(
    viewModel: AgrivaViewModel,
    modifier: Modifier = Modifier
) {
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val authError by viewModel.authStateError.collectAsState()

    var email by remember { mutableStateOf("fermier@agriva.mg") }
    var password by remember { mutableStateOf("123456") }
    var selectedRole by remember { mutableStateOf("USER") } // "USER" or "ADMIN"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AgrivaDarkGreen,
                        AgrivaGreen.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header Logo and Slogan
                AgrivaLogo(
                    size = 110f,
                    textSize = 28f,
                    showText = true,
                    textColor = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Plateforme Agricole Intelligente de Madagascar",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Role Selector with Segmented Style Visuals
                Text(
                    text = "Mifidiana Andraikitra (Rôle) :",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val roles = listOf("USER", "ADMIN")
                    roles.forEach { role ->
                        val isSelected = selectedRole == role
                        val buttonColor = if (isSelected) AgrivaGreen else Color.Transparent
                        val contentColor =
                            if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                        Button(
                            onClick = {
                                selectedRole = role
                                // Preset matching email strings for convenience
                                if (role == "ADMIN") {
                                    email = "admin@agriva.mg"
                                } else {
                                    email = "fermier@agriva.mg"
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("role_${role.lowercase()}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                contentColor = contentColor
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (role == "ADMIN") Icons.Default.Shield else Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (role == "ADMIN") "ADMIN (Sariako)" else "Fermier (USER)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Email Form Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail (Adiresy mailaka)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = AgrivaGreen
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgrivaGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password Form Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Teny miafina (Mot de passe)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AgrivaGreen
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AgrivaGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Error Message Panel (Animated)
                AnimatedVisibility(
                    visible = authError != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    authError?.let { err ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        viewModel.login(email, selectedRole)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_login_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AgrivaGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isAuthenticating
                ) {
                    if (isAuthenticating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Hiditra amin'ny Plateforme",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Offline-First Status Pill indicator
                Box(
                    modifier = Modifier
                        .background(
                            AgrivaGreen.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Protocole sécurisé : Offline-first synchro vaky",
                        fontSize = 10.sp,
                        color = AgrivaGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
