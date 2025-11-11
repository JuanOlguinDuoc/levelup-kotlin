package com.example.levelup.ui.theme.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.ProductModel
import com.example.levelup.ui.theme.DarkGreen
import com.example.levelup.ui.theme.mainTopBar.MainTopBar
import com.example.levelup.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val viewModel = remember { ProductViewModel(dataStoreManager, context) }
    val productos by viewModel.productos.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editarProducto by remember { mutableStateOf<ProductModel?>(null) }

    Scaffold(
        topBar = {
            MainTopBar(
                title = "Productos",
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color.Green)
                )
            ) {
                Text("Agregar nuevo producto", style = MaterialTheme.typography.titleMedium)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productos) { producto ->
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
                            Column {
                                Text(producto.name, style = MaterialTheme.typography.titleLarge)
                                Text(
                                    "Precio: $${producto.price}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Stock: ${producto.stock}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    producto.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row {
                                IconButton(onClick = {
                                    editarProducto = producto
                                    showEditDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar producto", tint = Color.Blue)
                                }

                                IconButton(onClick = { viewModel.eliminarProducto(producto.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar producto", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showEditDialog && editarProducto != null) {
            EditarProductoDialog(
                producto = editarProducto!!,
                onDismiss = { showEditDialog = false },
                onConfirm = { name, description, price, stock ->
                    viewModel.editarProducto(
                        id = editarProducto!!.id,
                        newName = name,
                        newDescription = description,
                        newPrice = price,
                        newStock = stock
                    )
                    showEditDialog = false
                }
            )
        }

        if (showDialog) {
            AgregarProductoDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, description, price, stock ->
                    val newId = if (productos.isEmpty()) 1 else productos.maxOf { it.id } + 1
                    val newProduct = ProductModel(
                        id = newId,
                        name = name,
                        description = description,
                        price = price,
                        stock = stock
                    )
                    viewModel.guardarProducto(newProduct)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AgregarProductoDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, price: Int, stock: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var ok = true
        if (!com.example.levelup.utils.ValidationUtils.isNonEmpty(name)) {
            nameError = "Nombre requerido"
            ok = false
        } else {
            nameError = null
        }

        val priceInt = com.example.levelup.utils.ValidationUtils.parsePositiveInt(price)
        if (priceInt == null) {
            priceError = "Precio inválido (>= 0)"
            ok = false
        } else {
            priceError = null
        }

        val stockInt = com.example.levelup.utils.ValidationUtils.parsePositiveInt(stock)
        if (stockInt == null) {
            stockError = "Stock inválido (>= 0)"
            ok = false
        } else {
            stockError = null
        }

        return ok
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null
                )
                if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = priceError != null
                )
                if (priceError != null) Text(priceError!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = stockError != null
                )
                if (stockError != null) Text(stockError!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (validate()) {
                        val priceInt = com.example.levelup.utils.ValidationUtils.parsePositiveInt(price) ?: 0
                        val stockInt = com.example.levelup.utils.ValidationUtils.parsePositiveInt(stock) ?: 0
                        onConfirm(name, description, priceInt, stockInt)
                    }
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
fun EditarProductoDialog(
    producto: ProductModel,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, price: Int, stock: Int) -> Unit
) {
    var name by remember { mutableStateOf(producto.name) }
    var description by remember { mutableStateOf(producto.description) }
    var price by remember { mutableStateOf(producto.price.toString()) }
    var stock by remember { mutableStateOf(producto.stock.toString()) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var ok = true
        if (!com.example.levelup.utils.ValidationUtils.isNonEmpty(name)) {
            nameError = "Nombre requerido"
            ok = false
        } else {
            nameError = null
        }

        val priceInt = com.example.levelup.utils.ValidationUtils.parsePositiveInt(price)
        if (priceInt == null) {
            priceError = "Precio inválido (>= 0)"
            ok = false
        } else {
            priceError = null
        }

        val stockInt = com.example.levelup.utils.ValidationUtils.parsePositiveInt(stock)
        if (stockInt == null) {
            stockError = "Stock inválido (>= 0)"
            ok = false
        } else {
            stockError = null
        }

        return ok
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null
                )
                if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = priceError != null
                )
                if (priceError != null) Text(priceError!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = stockError != null
                )
                if (stockError != null) Text(stockError!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (validate()) {
                        val priceInt = com.example.levelup.utils.ValidationUtils.parsePositiveInt(price) ?: 0
                        val stockInt = com.example.levelup.utils.ValidationUtils.parsePositiveInt(stock) ?: 0
                        onConfirm(name, description, priceInt, stockInt)
                    }
                }
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
