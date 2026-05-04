package com.tecsup.stockmin.ui.screens.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.stockmin.domain.model.Categoria
import com.tecsup.stockmin.viewmodel.FormItemEvento
import com.tecsup.stockmin.viewmodel.FormItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormItemScreen(
    viewModel: FormItemViewModel,
    onAtras: () -> Unit,
    onGuardado: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.guardado) {
        if (state.guardado) onGuardado()
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.onEvento(FormItemEvento.LimpiarError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.esEdicion) "Editar item" else "Nuevo item")
                },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Datos del item",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.onEvento(FormItemEvento.CambiarNombre(it)) },
                label = { Text("Nombre del item") },
                placeholder = { Text("Ej. Taladro percutor") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.nombre.isNotEmpty() && !state.nombreValido,
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                "Categoria",
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Categoria.values().forEach { categoria ->
                    FilterChip(
                        selected = state.categoria == categoria,
                        onClick = { viewModel.onEvento(FormItemEvento.CambiarCategoria(categoria)) },
                        label = { Text(categoria.etiqueta) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.stockActual,
                    onValueChange = { viewModel.onEvento(FormItemEvento.CambiarStockActual(it)) },
                    label = { Text("Stock actual") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = state.stockActual.isNotEmpty() && !state.stockActualValido,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = state.stockMinimo,
                    onValueChange = { viewModel.onEvento(FormItemEvento.CambiarStockMinimo(it)) },
                    label = { Text("Stock minimo") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = state.stockMinimo.isNotEmpty() && !state.stockMinimoValido,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.onEvento(FormItemEvento.Guardar) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = state.formularioValido && !state.cargando,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text(
                    if (state.esEdicion) "  Actualizar item" else "  Guardar item",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text(
                "Tip: el stock minimo es el umbral que dispara la alerta visual en el catalogo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
