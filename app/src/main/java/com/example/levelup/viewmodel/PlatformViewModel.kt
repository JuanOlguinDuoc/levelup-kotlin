package com.example.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.PlatformModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

class PlatformViewModel(
    private val dataStoreManager : DataStoreManager,
    private val context: Context
): ViewModel() {

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

    val plataformas = dataStoreManager.platforms.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun guardarPlataforma(plataforma : PlatformModel){
        viewModelScope.launch {
            val plataformasActuales = plataformas.value.toMutableList()
            if (plataformasActuales.none { it.id == plataforma.id}) {
                plataformasActuales.add(plataforma)
                dataStoreManager.savePlatform(plataformasActuales)
                playSuccessSound()
            }
        }
    }

    fun eliminarPlataforma(id: Long){
        viewModelScope.launch {
            val plataformasActuales = plataformas.value.filterNot { it.id == id }
            dataStoreManager.savePlatform(plataformasActuales)
        }
    }

    fun editarPlataforma (id: Long, newName: String){
        viewModelScope.launch {
            val plataformasActuales = plataformas.value.toMutableList()
            val index = plataformasActuales.indexOfFirst { it.id == id }
            if (index != -1){
                val plataformaEditada = plataformasActuales[index].copy(
                    name = newName
                )
                plataformasActuales[index] = plataformaEditada
                dataStoreManager.savePlatform(plataformasActuales)
            }
        }
    }


}