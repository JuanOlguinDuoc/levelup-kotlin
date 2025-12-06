package com.example.levelup.ui.theme.category

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
import com.example.levelup.model.CategoryModel
import com.example.levelup.ui.theme.DarkGreen
import com.example.levelup.ui.theme.mainTopBar.MainTopBar
import com.example.levelup.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
){
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val viewModel = remember { CategoryViewModel(dataStoreManager, context) }
    val categories by viewModel.categories.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editarCategoria by remember { mutableStateOf<CategoryModel?>(null) }

    Scaffold(
        topBar = {
            MainTopBar(
                title = "Categorías",
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
                items(categories) { category ->
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
                                    imageVector = getCategoryIcon(category.icon),
                                    contentDescription = category.description,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Column {
                                    Text(category.name, style = MaterialTheme.typography.titleLarge)
                                    Text(category.description, style = MaterialTheme.typography.bodyMedium)
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row {
                                IconButton(onClick = {
                                    editarCategoria = category
                                    showEditDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar categoría", tint = Color.Blue )
                                }

                                IconButton(onClick = { viewModel.eliminarCategoria(category.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar categoría", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showEditDialog && editarCategoria != null) {
            editarCategoriaDialog(
                category = editarCategoria!!,
                onDismiss = { showEditDialog = false },
                onConfirm = { newName, newDescription, newIcon ->
                    viewModel.editarCategoria(
                        id = editarCategoria!!.id,
                        newName = newName,
                        newDescription = newDescription,
                        newIcon = newIcon
                    )
                    showEditDialog = false
                }
            )
        }

        if (showDialog) {
            AddCategoryDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, description, icon ->
                    val newId = if (categories.isEmpty()) 1 else categories.maxOf { it.id } + 1
                    val newCategory = CategoryModel(newId, name, description, icon)
                    viewModel.guardarCategoria(newCategory)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, icon: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("juegos") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva categoría") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it },
                    label = { Text("Icono (juegos, pc, etc.)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, description, icon) }) {
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
    category: CategoryModel,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, icon: String) -> Unit
) {
    var name by remember { mutableStateOf(category.name) }
    var description by remember { mutableStateOf(category.description) }
    var icon by remember { mutableStateOf(category.icon) }

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
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it },
                    label = { Text("Icono (juegos, pc, etc.)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, description, icon) }) {
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
