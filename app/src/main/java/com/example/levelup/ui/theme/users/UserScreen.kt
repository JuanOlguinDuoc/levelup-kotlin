package com.example.levelup.ui.theme.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.CategoryModel
import com.example.levelup.model.UserModel
import com.example.levelup.ui.theme.DarkGreen
import com.example.levelup.ui.theme.mainTopBar.MainTopBar
import com.example.levelup.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
){
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val viewModel = remember { UserViewModel(dataStoreManager, context) }
    val users by viewModel.usuarios.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editarUser by remember { mutableStateOf<UserModel?>(null) }

    Scaffold (
        topBar = {
            MainTopBar(
                title = "Usuarios",
                onMenuClick = onMenuClick
            )
        }
    ){
        innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(androidx.compose.ui.graphics.Color.Green)
                )
            ) {
                Text("Agregar nuevo", style = MaterialTheme.typography.titleMedium)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = user.nombres + " "+ user.apellidos,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Column {
                                    Text(user.nombres + " "+ user.apellidos, style = MaterialTheme.typography.titleLarge)
                                    Text(user.run, style = MaterialTheme.typography.bodyMedium)
                                    Text(user.direccion, style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row {
                                IconButton(onClick = {
                                    editarUser = user
                                    showEditDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar usuario", tint = Color.Blue)
                                }

                                IconButton(onClick = { viewModel.eliminarUsuario(user.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar usuario", tint = Color.Red)
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
                onConfirm = {newRun , newDireccion, newName, newApellido, newCorreo, newPassword ->
                    viewModel.editarUsuario(
                        editarUser!!.id,
                        newRun,
                        newDireccion,
                        newName,
                        newApellido,
                        newCorreo,
                        newPassword
                    )
                    showEditDialog = false
                }
            )
        }

        if (showDialog) {
            AddUserDialog(
                onDismiss = { showDialog = false },
                onConfirm = { newRun, newDireccion, newName, newApellido, newCorreo, newPassword ->
                    val newId = if (users.isEmpty()) 1 else users.maxOf { it.id } + 1
                    val newUser = UserModel(
                        id = newId,
                        run = newRun,
                        nombres = newName,
                        apellidos = newApellido,
                        direccion = newDireccion,
                        correo = newCorreo,
                        password = newPassword
                    )
                    viewModel.guardarUsuario(newUser)
                    showDialog = false
                }
            )
        }

    }
}

@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (run: String, direccion: String, nombres: String, apellidos: String, correo: String, password: String) -> Unit
) {
    var run by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo usuario") },
        text = {
            Column {
                OutlinedTextField(value = run, onValueChange = { run = it }, label = { Text("Run") })
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres") })
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") })
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") })
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") })
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(run, direccion, nombres, apellidos, correo, password)
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}



@Composable
fun editarUserDialog(
    user: UserModel,
    onDismiss: () -> Unit,
    onConfirm: (run: String, nombres: String, apellidos: String, direccion: String, correo: String, password: String) -> Unit
) {
    var run by remember { mutableStateOf(user.run) }
    var nombres by remember { mutableStateOf(user.nombres) }
    var apellidos by remember { mutableStateOf(user.apellidos) }
    var direccion by remember { mutableStateOf(user.direccion) }
    var correo by remember { mutableStateOf(user.correo) }
    var password by remember { mutableStateOf(user.password) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar categoría") },
        text = {
            Column {
                OutlinedTextField(
                    value = run,
                    onValueChange = { run = it },
                    label = { Text("Run") }
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombres") }
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") }
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Direccion") }
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") }
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(run, nombres, apellidos, direccion, correo, password) }) {
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
