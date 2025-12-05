package com.example.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings
import com.example.levelup.model.UserModel
import com.example.levelup.model.RegisterRequest
import com.example.levelup.model.ApiResult
import com.example.levelup.repository.LevelUpRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModelWithApi(
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
    val usuarios: StateFlow<List<UserModel>> = repository.getUsersFromLocal()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        // Load users from API when ViewModel is created
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.getUsers().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        _isLoading.value = false
                        // Data is automatically saved to local storage by repository
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

    fun guardarUsuario(usuario: UserModel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Verificar campos requeridos (sin direccion que no va al backend)
            if (usuario.run.isBlank() || usuario.firstName.isBlank() || usuario.lastName.isBlank() ||
                usuario.email.isBlank() || usuario.password.isBlank() || usuario.role.isBlank()) {
                _isLoading.value = false
                _errorMessage.value = "Todos los campos son requeridos"
                return@launch
            }
            
            // Crear request directamente del UserModel actualizado
            val request = RegisterRequest(
                run = usuario.run.trim(),
                firstName = usuario.firstName.trim(),
                lastName = usuario.lastName.trim(), 
                email = usuario.email.trim(),
                password = usuario.password,
                role = usuario.role.ifBlank { "usuario" }
            )
            
            println("DEBUG: Enviando usuario - RUN: ${request.run}, FirstName: ${request.firstName}, Email: ${request.email}, Rol: ${request.role}")
            
            when (val result = repository.createUser(request)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                    println("DEBUG: Usuario creado exitosamente - ${result.data.message}")
                    // Recargar la lista de usuarios desde el backend
                    loadUsers()
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.exception.message
                    println("DEBUG: Error al crear usuario - ${result.exception.message}")
                }
                is ApiResult.Loading -> {}
            }
        }
    }

    fun actualizarUsuario(usuario: UserModel) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = repository.updateUser(usuario.id, usuario)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                    println("DEBUG: Usuario actualizado exitosamente - ${result.data.message}")
                    loadUsers()
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

    fun eliminarUsuario(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            when (val result = repository.deleteUser(id)) {
                is ApiResult.Success -> {
                    _isLoading.value = false
                    playSuccessSound()
                    println("DEBUG: Usuario eliminado exitosamente - ${result.data.message}")
                    loadUsers()
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
