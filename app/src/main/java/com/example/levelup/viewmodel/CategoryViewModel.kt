package com.example.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.CategoryModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

class CategoryViewModel(
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {

    private fun playSuccessSound() {
        try {
            // Only create MediaPlayer if we're not in test mode
            if (!isInTestMode()) {
                val mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI)
                mediaPlayer?.let { player ->
                    player.start()
                    player.setOnCompletionListener { mp ->
                        mp.release()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isInTestMode(): Boolean {
        return try {
            Class.forName("org.junit.Test")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    // Observa el flujo de categorías como StateFlow
    val categories = dataStoreManager.categories.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun guardarCategoria(categoria: CategoryModel) {
        viewModelScope.launch {
            val categoriasActuales = categories.value.toMutableList()
            if (categoriasActuales.none{it.id == categoria.id}){
                categoriasActuales.add(categoria)
                dataStoreManager.saveCategories(categoriasActuales)
                playSuccessSound()
            }
        }
    }

    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            val categoriasActuales = categories.value.filterNot { it.id == id }
            dataStoreManager.saveCategories(categoriasActuales)
        }
    }

    fun editarCategoria(id: Long, newName: String, newDescription: String, newIcon: String) {
        viewModelScope.launch {
            val categoriasActuales = categories.value.toMutableList()
            val index = categoriasActuales.indexOfFirst { it.id == id }
            if (index != -1) {
                val categoriaEditada = categoriasActuales[index].copy(
                    name = newName,
                    description = newDescription,
                    icon = newIcon
                )
                categoriasActuales[index] = categoriaEditada
                dataStoreManager.saveCategories(categoriasActuales)
            }
        }
    }
}
