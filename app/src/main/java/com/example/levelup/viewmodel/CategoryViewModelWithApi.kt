package com.example.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings
import com.example.levelup.model.CategoryModel
import com.example.levelup.model.ApiResult
import com.example.levelup.repository.LevelUpRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModelWithApi(
    private val context: Context
): ViewModel() {

    private val repository = LevelUpRepository(context)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private fun playSuccessSound() {
        try {
            val mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI)
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

    // Local data for UI
    val categorias: StateFlow<List<CategoryModel>> = repository.getCategoriesFromLocal()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        // Load categories from API when ViewModel is created
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.getCategories().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _isLoading.value = false
                    }
                    is ApiResult.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = result.exception.message
                    }
                    is ApiResult.Loading -> {
                        _isLoading.value = result.isLoading
                    }
                }
            }
        }
    }

    fun guardarCategoria(categoria: CategoryModel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = repository.createCategory(categoria)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                }
                is ApiResult.Loading -> {
                    // Handled by _isLoading
                }
            }
        }
    }

    fun actualizarCategoria(categoria: CategoryModel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = repository.updateCategory(categoria.id, categoria)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                }
                is ApiResult.Loading -> {
                    // Handled by _isLoading
                }
            }
        }
    }

    fun eliminarCategoria(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = repository.deleteCategory(id)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                }
                is ApiResult.Loading -> {
                    // Handled by _isLoading
                }
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
