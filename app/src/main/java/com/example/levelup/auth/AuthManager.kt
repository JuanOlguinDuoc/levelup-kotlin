package com.example.levelup.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.levelup.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthManager(private val context: Context) {
    
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val EMAIL_KEY = stringPreferencesKey("user_email")
    }
    
    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }
    
    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
        }
    }
    
    fun getToken(): Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[TOKEN_KEY] }
    
    fun getUserEmail(): Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[EMAIL_KEY] }
    
    suspend fun clearAuth() {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(EMAIL_KEY)
        }
    }
    
    fun getBearerToken(token: String): String = "Bearer $token"
}
