package com.example.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings
import com.example.levelup.model.Carrito
import com.example.levelup.model.CartProduct
import com.example.levelup.model.ApiResult
import com.example.levelup.repository.LevelUpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarritoViewModel(private val context: Context) : ViewModel() {

    private val repository = LevelUpRepository(context)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _carts = MutableStateFlow<List<Carrito>>(emptyList())
    val carts: StateFlow<List<Carrito>> = _carts.asStateFlow()

    private fun playSuccessSound() {
        try {
            // Only create MediaPlayer if we're not in test mode
            if (!isInTestMode()) {
                val mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI)
                mediaPlayer?.let { player ->
                    player.start()
                    player.setOnCompletionListener { mp -> mp.release() }
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

    init {
        loadAllCarts()
    }

    fun loadAllCarts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = repository.getAllCarts()) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _carts.value = result.data
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

    fun getCartById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.getCartById(id)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    _carts.value = listOf(result.data)
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

    fun createCart(userId: Long, products: List<CartProduct>) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val newCart = Carrito(userId = userId, date = java.time.Instant.now().toString(), products = products)
            when (val result = repository.createCart(newCart)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                    // refresh list
                    loadAllCarts()
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun deleteCart(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.deleteCart(id)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                    loadAllCarts()
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun updateCart(id: Long, updated: Carrito) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            when (val result = repository.updateCart(id, updated)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                    loadAllCarts()
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun clearError() { _errorMessage.value = null }
}
