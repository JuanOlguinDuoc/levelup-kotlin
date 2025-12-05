package com.example.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup.auth.AuthManager
import com.example.levelup.repository.LevelUpRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.content.Context

class AuthViewModel(context: Context) : ViewModel() {
    
    private val authManager = AuthManager(context)
    private val repository = LevelUpRepository(context)
    
    val isLoggedIn: StateFlow<Boolean> = authManager.getToken()
        .map { token -> !token.isNullOrBlank() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    
    val userEmail: StateFlow<String?> = authManager.getUserEmail()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
