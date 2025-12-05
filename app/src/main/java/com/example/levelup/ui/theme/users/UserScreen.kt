package com.example.levelup.ui.theme.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.levelup.model.UserModel
import com.example.levelup.ui.theme.DarkGreen
import com.example.levelup.ui.theme.mainTopBar.MainTopBar
import com.example.levelup.viewmodel.UserViewModelWithApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(modifier: Modifier = Modifier, onMenuClick: () -> Unit = {}) {
    val context = LocalContext.current
    val viewModel = remember { UserViewModelWithApi(context) }
    val users by viewModel.usuarios.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editarUser by remember { mutableStateOf<UserModel?>(null) }

    // Limpiar error cuando se cierre el mensaje
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(topBar = { MainTopBar(title = "Usuarios", onMenuClick = onMenuClick) }) { innerPadding
        ->
        Column(
                modifier = modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje de error
            errorMessage?.let { error ->
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                ) {
                    Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            OutlinedButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                    border =
                            ButtonDefaults.outlinedButtonBorder.copy(
                                    brush =
                                            androidx.compose.ui.graphics.SolidColor(
                                                    androidx.compose.ui.graphics.Color.Green
                                            )
                            )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = DarkGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Agregar nuevo", style = MaterialTheme.typography.titleMedium)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isLoading && users.isEmpty()) {
                    item {
                        Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = DarkGreen) }
                    }
                }

                items(users) { user ->
                    Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.surfaceVariant
                                    )
                    ) {
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = user.nombres + " " + user.apellidos,
                                        modifier = Modifier.padding(end = 8.dp)
                                )
                                Column {
                                    Text(
                                            user.nombres + " " + user.apellidos,
                                            style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(user.run, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                            user.direccion,
                                            style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                            user.correo,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row {
                                IconButton(
                                        onClick = {
                                            editarUser = user
                                            showEditDialog = true
                                        },
                                        enabled = !isLoading
                                ) {
                                    Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar usuario",
                                            tint = Color.Blue
                                    )
                                }

                                IconButton(
                                        onClick = { viewModel.eliminarUsuario(user.id) },
                                        enabled = !isLoading
                                ) {
                                    Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar usuario",
                                            tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showEditDialog && editarUser != null) {
            editarUserDialog(
                    user = editarUser!!,
                    onDismiss = { showEditDialog = false },
                    onConfirm = { updatedUser ->
                        viewModel.actualizarUsuario(updatedUser)
                        showEditDialog = false
                    }
            )
        }
        if (showDialog) {
            AddUserDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { newUser ->
                        viewModel.guardarUsuario(newUser)
                        showDialog = false
                    }
            )
        }
        }
    }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (UserModel) -> Unit
) {
    var run by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }    // Cambiar nombres
    var lastName by remember { mutableStateOf("") }     // Cambiar apellidos  
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }        // Cambiar correo
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("usuario") }  // Valor por defecto correcto
    var expanded by remember { mutableStateOf(false) }

    val roles = listOf("usuario", "administrador", "cliente")
    
    // Validaciones
    val isFormValid = run.isNotBlank() && firstName.isNotBlank() && 
                     lastName.isNotBlank() && email.isNotBlank() && 
                     password.isNotBlank() && role.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Usuario") },
        text = {
            Column {
                // RUN
                OutlinedTextField(
                    value = run,
                    onValueChange = { run = it },
                    label = { Text("RUN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // First Name
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Nombres") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Last Name  
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apellidos") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Direccion (solo para uso local)
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { selectedRole ->
                            DropdownMenuItem(
                                onClick = {
                                    role = selectedRole
                                    expanded = false
                                },
                                text = { Text(selectedRole.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newUser = UserModel(
                        id = 0,
                        run = run,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password,
                        role = role,
                        direccion = direccion
                    )
                    onConfirm(newUser)
                    onDismiss()
                },
                enabled = isFormValid
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editarUserDialog(
    user: UserModel,
    onDismiss: () -> Unit,
    onConfirm: (UserModel) -> Unit
) {
    var run by remember { mutableStateOf(user.run) }
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var direccion by remember { mutableStateOf(user.direccion) }
    var email by remember { mutableStateOf(user.email) }
    var password by remember { mutableStateOf(user.password) }
    var role by remember { mutableStateOf(user.role) }
    var expanded by remember { mutableStateOf(false) }

    val roles = listOf("usuario", "administrador", "cliente")
    
    // Validaciones
    val isFormValid = run.isNotBlank() && firstName.isNotBlank() && 
                     lastName.isNotBlank() && email.isNotBlank() && 
                     password.isNotBlank() && role.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Usuario") },
        text = {
            Column {
                // RUN
                OutlinedTextField(
                    value = run,
                    onValueChange = { run = it },
                    label = { Text("RUN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // First Name
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Nombres") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Last Name  
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apellidos") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Direccion (solo para uso local)
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Role Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { selectedRole ->
                            DropdownMenuItem(
                                onClick = {
                                    role = selectedRole
                                    expanded = false
                                },
                                text = { Text(selectedRole.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedUser = user.copy(
                        run = run,
                        firstName = firstName,
                        lastName = lastName,
                        direccion = direccion,
                        email = email,
                        password = password,
                        role = role
                    )
                    onConfirm(updatedUser)
                },
                enabled = isFormValid
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
