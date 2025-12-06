package com.example.levelup.ui.theme.carrito

// Carrito screen composable: created to list and manage carts via LevelUpRepository

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.levelup.viewmodel.CarritoViewModel
import com.example.levelup.ui.theme.mainTopBar.MainTopBar
import com.example.levelup.model.Carrito
import com.example.levelup.model.CartProduct
import com.example.levelup.utils.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { CarritoViewModel(context) }
    val carts by viewModel.carts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingCart by remember { mutableStateOf<Carrito?>(null) }

    Scaffold(
        topBar = {
            MainTopBar(title = "Ventas", onMenuClick = onMenuClick)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.loadAllCarts() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    Spacer(Modifier.width(8.dp))
                    Text("Refrescar")
                }

                OutlinedButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Crear")
                    Spacer(Modifier.width(8.dp))
                    Text("Nuevo carrito")
                }
            }

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (errorMessage != null) {
                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(carts) { cart ->
                    CartCard(cart = cart, onDelete = { viewModel.deleteCart(it) }, onEdit = {
                        editingCart = it
                        showEditDialog = true
                    })
                }
            }
        }

        if (showCreateDialog) {
            CreateCartDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { userIdStr, productIdStr, quantityStr ->
                    val userId = com.example.levelup.utils.ValidationUtils.parsePositiveInt(userIdStr)?.toLong() ?: 0L
                    val productId = com.example.levelup.utils.ValidationUtils.parsePositiveInt(productIdStr)?.toLong() ?: 0L
                    val quantity = com.example.levelup.utils.ValidationUtils.parsePositiveInt(quantityStr) ?: 1
                    val products = listOf(CartProduct(productId = productId, quantity = quantity))
                    viewModel.createCart(userId, products)
                    showCreateDialog = false
                }
            )
        }

        if (showEditDialog && editingCart != null) {
            EditCartDialog(
                cart = editingCart!!,
                onDismiss = { showEditDialog = false; editingCart = null },
                onConfirm = { newUserIdStr, newQuantityStr ->
                    val newUserId = com.example.levelup.utils.ValidationUtils.parsePositiveInt(newUserIdStr)?.toLong() ?: editingCart!!.userId
                    val newQuantity = com.example.levelup.utils.ValidationUtils.parsePositiveInt(newQuantityStr) ?: (editingCart!!.products.firstOrNull()?.quantity ?: 1)
                    val firstProduct = editingCart!!.products.firstOrNull()
                    val newProducts = if (firstProduct != null) listOf(CartProduct(productId = firstProduct.productId, quantity = newQuantity)) else emptyList()
                    val updated = editingCart!!.copy(userId = newUserId, products = newProducts)
                    viewModel.updateCart(editingCart!!.id ?: 0L, updated)
                    showEditDialog = false
                    editingCart = null
                }
            )
        }
    }
}

@Composable
fun CartCard(cart: Carrito, onDelete: (Long) -> Unit, onEdit: (Carrito) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Cart #${cart.id ?: "-"}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onEdit(cart) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Blue)
                }

                IconButton(onClick = { cart.id?.let { onDelete(it) } }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }

            Text("User: ${cart.userId}")
            Text("Date: ${cart.date}")

            Spacer(Modifier.height(8.dp))
            Text("Productos:")
            for (p in cart.products) {
                Text("- ${p.productId} x ${p.quantity}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCartDialog(onDismiss: () -> Unit, onConfirm: (userId: String, productId: String, quantity: String) -> Unit) {
    var userId by remember { mutableStateOf("") }
    var productId by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    var userIdError by remember { mutableStateOf<String?>(null) }
    var productIdError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var ok = true
    if (!com.example.levelup.utils.ValidationUtils.isNonEmpty(userId) || com.example.levelup.utils.ValidationUtils.parsePositiveInt(userId) == null) {
            userIdError = "UserId inválido"
            ok = false
        } else userIdError = null

    if (!com.example.levelup.utils.ValidationUtils.isNonEmpty(productId) || com.example.levelup.utils.ValidationUtils.parsePositiveInt(productId) == null) {
            productIdError = "ProductId inválido"
            ok = false
        } else productIdError = null

        if (!ValidationUtils.isNonEmpty(quantity) || ValidationUtils.parsePositiveInt(quantity) == null) {
            quantityError = "Cantidad inválida"
            ok = false
        } else quantityError = null

        return ok
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Carrito") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("UserId") }, isError = userIdError != null)
                if (userIdError != null) Text(userIdError!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(value = productId, onValueChange = { productId = it }, label = { Text("ProductId") }, isError = productIdError != null)
                if (productIdError != null) Text(productIdError!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Cantidad") }, isError = quantityError != null)
                if (quantityError != null) Text(quantityError!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = { if (validate()) onConfirm(userId, productId, quantity) }) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCartDialog(cart: Carrito, onDismiss: () -> Unit, onConfirm: (newUserId: String, newQuantity: String) -> Unit) {
    var userId by remember { mutableStateOf(cart.userId.toString()) }
    var quantity by remember { mutableStateOf(cart.products.firstOrNull()?.quantity?.toString() ?: "1") }

    var userIdError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var ok = true
        if (!com.example.levelup.utils.ValidationUtils.isNonEmpty(userId) || com.example.levelup.utils.ValidationUtils.parsePositiveInt(userId) == null) {
            userIdError = "UserId inválido"
            ok = false
        } else userIdError = null

        if (!com.example.levelup.utils.ValidationUtils.isNonEmpty(quantity) || com.example.levelup.utils.ValidationUtils.parsePositiveInt(quantity) == null) {
            quantityError = "Cantidad inválida"
            ok = false
        } else quantityError = null

        return ok
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Carrito") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("UserId") }, isError = userIdError != null)
                if (userIdError != null) Text(userIdError!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Cantidad (primer producto)") }, isError = quantityError != null)
                if (quantityError != null) Text(quantityError!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = { if (validate()) onConfirm(userId, quantity) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
