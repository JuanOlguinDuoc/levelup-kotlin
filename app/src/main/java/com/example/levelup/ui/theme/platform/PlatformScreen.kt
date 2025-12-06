package com.example.levelup.ui.theme.platform

import androidx.compose.ui.graphics.SolidColor
import com.example.levelup.ui.theme.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.PlatformModel
import com.example.levelup.ui.theme.DarkGreen
import com.example.levelup.ui.theme.mainTopBar.MainTopBar
import com.example.levelup.viewmodel.PlatformViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
){
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val viewModel = remember { PlatformViewModel(dataStoreManager, context) }
    val platforms by viewModel.plataformas.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editarPlataforma by remember { mutableStateOf<PlatformModel?>(null) }

    Scaffold(
        topBar = {
            MainTopBar(
                title = "Plataformas",
                onMenuClick = onMenuClick
            )
        }
    ) { innerPadding ->
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Pink80),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = SolidColor(Green80)
                )
            ) {
                Text("Agregar nuevo", style = MaterialTheme.typography.titleMedium)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(platforms) { platform ->
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
                                Column {
                                    Text(platform.name, style = MaterialTheme.typography.titleLarge)
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row {
                                IconButton(onClick = {
                                    editarPlataforma= platform
                                    showEditDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar categoría", tint = Color.Blue )
                                }

                                IconButton(onClick = { viewModel.eliminarPlataforma(platform.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar categoría", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showEditDialog && editarPlataforma != null) {
            editarCategoriaDialog(
                platform = editarPlataforma!!,
                onDismiss = { showEditDialog = false },
                onConfirm = { newName ->
                    viewModel.editarPlataforma(
                        id = editarPlataforma!!.id,
                        newName = newName
                    )
                    showEditDialog = false
                }
            )
        }

        if (showDialog) {
            AddCategoryDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name ->
                    val newId = if (platforms.isEmpty()) 1 else platforms.maxOf { it.id } + 1
                    val newPlatform = PlatformModel(newId, name)
                    viewModel.guardarPlataforma(newPlatform)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva plataforma") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
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
fun editarCategoriaDialog(
    platform: PlatformModel,
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit
) {
    var name by remember { mutableStateOf(platform.name) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar categoría") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
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


fun getCategoryIcon(nombre: String): ImageVector {
    return when (nombre.lowercase()) {
        "juegos" -> Icons.Default.VideogameAsset
        "perifericos" -> Icons.Default.Keyboard
        "consolas" -> Icons.Default.SportsEsports
        "pc" -> Icons.Default.Computer
        "accesorios" -> Icons.Default.Headphones
        else -> Icons.Default.Warning
    }
}
