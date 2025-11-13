package com.example.levelup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelup.datastore.DataStoreManager
import com.example.levelup.model.UserModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import android.content.Context
import android.media.MediaPlayer
import android.provider.Settings

class UserViewModel(
    private val dataStorageManager : DataStoreManager,
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

    val usuarios = dataStorageManager.users.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun guardarUsuario(usuario: UserModel){
        viewModelScope.launch {
            val usuariosActuales = usuarios.value.toMutableList()
            if (usuariosActuales.none { it.id == usuario.id }){
                usuariosActuales.add(usuario)
                dataStorageManager.saveUsers(usuariosActuales)
                playSuccessSound()
            }
        }
    }

    fun eliminarUsuario(id: Int){
        viewModelScope.launch {
            val usuariosActuales = usuarios.value.filterNot { it.id == id }
            dataStorageManager.saveUsers(usuariosActuales)
        }
    }

    fun editarUsuario(id: Int, newRun: String, newDireccion: String, newName: String, newApellido: String, newCorreo: String, newPassword: String){
        viewModelScope.launch {
            val usuariosActuales = usuarios.value.toMutableList()
            val index = usuariosActuales.indexOfFirst { it.id == id }
            if ( index != -1) {
                val usuarioEditado = usuariosActuales[index].copy(
                    run = newRun,
                    direccion = newDireccion,
                    nombres = newName,
                    apellidos = newApellido,
                    correo = newCorreo,
                    password = newPassword
                )
                usuariosActuales[index] = usuarioEditado
                dataStorageManager.saveUsers(usuariosActuales)
            }
        }
    }


}