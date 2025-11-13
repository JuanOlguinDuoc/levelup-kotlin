package com.example.levelup.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.levelup.model.CategoryModel
import com.example.levelup.model.PlatformModel
import com.example.levelup.model.ProductModel
import com.example.levelup.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// Instancia global de DataStore (solo una vez)
val Context.dataStore by preferencesDataStore(name = "app_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
        val CATEGORIES_KEY = stringPreferencesKey("categories")

        val USERS_KEY = stringPreferencesKey("users")

        val PLATFORM_KEY = stringPreferencesKey("platforms")

        val PRODUCT_KEY = stringPreferencesKey("products")

    }

    suspend fun saveCategories(categories: List<CategoryModel>) {
        val json = Json.encodeToString(categories)
        context.dataStore.edit { prefs ->
            prefs[CATEGORIES_KEY] = json
        }
    }

    val categories: Flow<List<CategoryModel>> = context.dataStore.data
        .map { prefs ->
            val json  = prefs[CATEGORIES_KEY] ?: "[]"
            try {
                Json.decodeFromString<List<CategoryModel>>(json)
            }catch ( e: Exception){
                emptyList()
            }
        }

    suspend fun saveUsers(users: List<UserModel>){
        val json = Json.encodeToString(users)
        context.dataStore.edit { prefs ->
            prefs[USERS_KEY] = json
        }
    }

    val users: Flow<List<UserModel>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[USERS_KEY] ?: "[]"
            try{
                Json.decodeFromString<List<UserModel>>(json)
            }catch (e : Exception) {
                emptyList()
            }
        }

    suspend fun savePlatform(platforms : List<PlatformModel> ){
        val json = Json.encodeToString(platforms)
        context.dataStore.edit { prefs ->
            prefs[PLATFORM_KEY] = json
        }
    }

    val platforms: Flow<List<PlatformModel>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[PLATFORM_KEY] ?: "[]"
            try {
                Json.decodeFromString<List<PlatformModel>>(json)
            }catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun saveProducts(products: List<ProductModel>) {
        val json = Json.encodeToString(products)
        context.dataStore.edit { prefs ->
            prefs[PRODUCT_KEY] = json
        }
    }

    val products: Flow<List<ProductModel>> = context.dataStore.data
        .map { prefs ->
            val json = prefs[PRODUCT_KEY] ?: "[]"
            try {
                Json.decodeFromString<List<ProductModel>>(json)
            } catch (e: Exception) {
                emptyList()
            }
        }
}
