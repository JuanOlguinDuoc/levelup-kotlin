package com.example.levelup.ui.theme.profile
import androidx.compose.ui.graphics.SolidColor
import com.example.levelup.ui.theme.*
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.levelup.R
import com.example.levelup.ui.theme.components.InfoItem
import com.example.levelup.ui.theme.mainTopBar.MainTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            MainTopBar(
                title = "Perfil",
                onMenuClick = onMenuClick
            )
        }
    ){ innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Card (
                modifier = modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ){
                Column (modifier = Modifier.padding( 16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Logo app",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    InfoItem(label = "Nombre", value = "Administrador")
                    InfoItem(label = "Correo", value = "admin@levelup.cl")
                    InfoItem(label = "Rol", value = "Administrador")
                    InfoItem(label = "Celular", value = "+56912345678")

                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* TODO: abrir pantalla de edición */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar Perfil", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(4.dp))


            Button(
                onClick = { /* TODO: abrir diálogo para cambiar contraseña */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cambiar Contraseña", style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(4.dp))


            val context = LocalContext.current
            OutlinedButton(
                onClick = { vibrate(context) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = androidx.compose.ui.graphics.Color.Red
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = SolidColor(Green80)
                )
            ) {
                Text("Cerrar Sesión", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}



private fun vibrate(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(100)
            }
        }
    } catch (e: Exception) {
        // Si hay algún error, simplemente lo ignoramos para evitar que la app se cierre
        e.printStackTrace()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview(){
    ProfileScreen()
}

