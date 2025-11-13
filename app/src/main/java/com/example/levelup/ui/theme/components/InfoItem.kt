package com.example.levelup.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // Etiqueta (alineada a la izquierda con ancho fijo)
        Text(
            text = "$label:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.width(110.dp) // 👈 ancho fijo para alinear todas las etiquetas
        )

        // Valor (ocupa el resto del espacio)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge
        )
    }
}