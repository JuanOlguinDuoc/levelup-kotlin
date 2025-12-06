package com.example.levelup.ui.theme.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.text.input.VisualTransformation
import com.example.levelup.model.ApiResult
import com.example.levelup.repository.LevelUpRepository
import com.example.levelup.ui.theme.*
import com.example.levelup.ui.theme.Purple80
import com.example.levelup.ui.theme.Pink80
import com.example.levelup.ui.theme.Purple40
import com.example.levelup.R
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.painter.ColorPainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    // Test hooks: provide a painter to avoid resource loading in unit tests,
    // or provide a fake repository implementation.
    testLogoPainter: Painter? = null,
    testRepository: LevelUpRepository? = null
) {
    val context = LocalContext.current
    val repository = testRepository ?: remember { LevelUpRepository(context) }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F5F5) // Color de fondo gris claro como en la imagen
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Use injected painter in tests to avoid accessing Android resources.
            val logoPainter = testLogoPainter ?: painterResource(id = R.drawable.logo)
            Image(
                painter = logoPainter,
                contentDescription = "Level UP Logo",
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Inicio de Sesión",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Tu plataforma de gestión de ecommerce",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Green80
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Green80,
                            focusedLabelColor = Green80,
                            focusedLeadingIconColor = Green80,
                            focusedTextColor = Green80,
                            unfocusedTextColor = Green80,
                            cursorColor = Green80,
                            unfocusedLabelColor = Green80,
                            unfocusedLeadingIconColor = Green80,
                            focusedPlaceholderColor = Green80,
                            unfocusedPlaceholderColor = Green80
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Password field con icono
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Green80
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = Green80
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Green80,
                            focusedLabelColor = Green80,
                            focusedLeadingIconColor = Green80,
                            focusedTextColor = Green80,
                            unfocusedTextColor = Green80,
                            cursorColor = Green80,
                            unfocusedLabelColor = Green80,
                            unfocusedLeadingIconColor = Green80,
                            focusedPlaceholderColor = Green80,
                            unfocusedPlaceholderColor = Green80
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login button - Siempre visible con diferentes estilos
                    val isFormValid = email.isNotBlank() && password.isNotBlank() && !isLoading
                    
                    Button(
                        onClick = {
                            if (isFormValid) {
                                isLoading = true
                                scope.launch {
                                    when (val result = repository.login(email, password)) {
                                        is ApiResult.Success -> {
                                            isLoading = false
                                            onLoginSuccess()
                                        }

                                        is ApiResult.Error -> {
                                            isLoading = false
                                            errorMessage = result.exception.message ?: "Error desconocido"
                                        }

                                        is ApiResult.Loading -> {
                                            // Already handled by isLoading state
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(56.dp),
                        enabled = true, // Siempre habilitado visualmente
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormValid) Purple80 else Purple80.copy(alpha = 0.5f),
                            contentColor = Color.White,
                            disabledContainerColor = Purple80.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Iniciando...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    "Iniciar Sesión",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (isFormValid) TextDecoration.None else TextDecoration.LineThrough
                                )
                            }
                        }
                    }
                    
                    // Error message
                        errorMessage?.let { error ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Backend info con estilo morado
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🌐 Conectado al Backend",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Purple40,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AWS Elastic Beanstalk",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

