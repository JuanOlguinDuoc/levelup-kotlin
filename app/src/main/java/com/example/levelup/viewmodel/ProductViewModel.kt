package com.example.levelup.viewmodel

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup.R
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.ProductModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {

    private fun playSuccessSound() {
        try {
            val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            mediaPlayer?.let { player ->
                player.start()
                player.setOnCompletionListener { mp ->
                    mp.release()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val productos = dataStoreManager.products.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun guardarProducto(producto: ProductModel) {
        viewModelScope.launch {
            val productosActuales = productos.value.toMutableList()
            if (productosActuales.none { it.id == producto.id }) {
                productosActuales.add(producto)
                dataStoreManager.saveProducts(productosActuales)
                playSuccessSound()
            }
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            val productosActuales = productos.value.filterNot { it.id == id }
            dataStoreManager.saveProducts(productosActuales)
        }
    }

    fun editarProducto(id: Int, newName: String, newDescription: String, newPrice: Int, newStock: Int) {
        viewModelScope.launch {
            val productosActuales = productos.value.toMutableList()
            val index = productosActuales.indexOfFirst { it.id == id }
            if (index != -1) {
                val productoEditado = productosActuales[index].copy(
                    name = newName,
                    description = newDescription,
                    price = newPrice,
                    stock = newStock
                )
                productosActuales[index] = productoEditado
                dataStoreManager.saveProducts(productosActuales)
            }
        }
    }
}
