package com.tecsup.stockmin.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.stockmin.domain.model.Item
import com.tecsup.stockmin.domain.model.Movimiento
import com.tecsup.stockmin.domain.model.TipoMovimiento
import com.tecsup.stockmin.ui.components.Fechas
import com.tecsup.stockmin.viewmodel.DetalleEvento
import com.tecsup.stockmin.viewmodel.DetalleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    onAtras: () -> Unit,
    onEditar: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.itemEliminado) {
        if (state.itemEliminado) onAtras()
    }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.onEvento(DetalleEvento.LimpiarMensaje)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del item") },
                navigationIcon = {
                    IconButton(onClick = onAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onEditar) { Icon(Icons.Default.Edit, "Editar") }
                    IconButton(onClick = { viewModel.onEvento(DetalleEvento.EliminarItem) }) {
                        Icon(Icons.Default.Delete, "Eliminar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (state.item != null) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.onEvento(DetalleEvento.AbrirDialogo) },
                    icon = { Icon(Icons.Default.SwapVert, null) },
                    text = { Text("Registrar movimiento") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        val item = state.item
        if (item == null) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("Cargando...", style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 12.dp,
                bottom = 96.dp
            )
        ) {
            item { TarjetaResumen(item) }
            item {
                Text(
                    "Historial de movimientos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (state.historial.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            "Sin movimientos aun. Registra el primero con el boton inferior.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(state.historial, key = { it.id }) { mov ->
                    MovimientoFila(mov)
                }
            }
        }
    }

    if (state.mostrarDialogoMovimiento && state.item != null) {
        DialogoMovimiento(
            cantidad = state.cantidad,
            trabajador = state.trabajador,
            tipo = state.tipoSeleccionado,
            procesando = state.procesando,
            onTipo = { viewModel.onEvento(DetalleEvento.CambiarTipo(it)) },
            onCantidad = { viewModel.onEvento(DetalleEvento.CambiarCantidad(it)) },
            onTrabajador = { viewModel.onEvento(DetalleEvento.CambiarTrabajador(it)) },
            onConfirmar = { viewModel.onEvento(DetalleEvento.ConfirmarMovimiento) },
            onCerrar = { viewModel.onEvento(DetalleEvento.CerrarDialogo) }
        )
    }
}

@Composable
private fun TarjetaResumen(item: Item) {
    val containerColor = if (item.bajoStock)
        MaterialTheme.colorScheme.errorContainer
    else
        MaterialTheme.colorScheme.primaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                item.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            AssistChip(
                onClick = {},
                label = { Text(item.categoria.etiqueta) }
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Stock actual", style = MaterialTheme.typography.labelMedium)
                    Text(
                        item.stockActual.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Stock minimo", style = MaterialTheme.typography.labelMedium)
                    Text(
                        item.stockMinimo.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (item.bajoStock) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Atencion: stock por debajo del minimo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun MovimientoFila(mov: Movimiento) {
    val esSalida = mov.tipo == TipoMovimiento.SALIDA
    val color = if (esSalida) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (esSalida) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = color
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (esSalida) "Salida (prestamo)" else "Entrada (devolucion)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Trabajador: ${mov.trabajador}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    Fechas.formatear(mov.fechaMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                (if (esSalida) "-" else "+") + mov.cantidad.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoMovimiento(
    cantidad: String,
    trabajador: String,
    tipo: TipoMovimiento,
    procesando: Boolean,
    onTipo: (TipoMovimiento) -> Unit,
    onCantidad: (String) -> Unit,
    onTrabajador: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Registrar movimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Selecciona el tipo de movimiento:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoMovimiento.values().forEach { t ->
                        FilterChip(
                            selected = tipo == t,
                            onClick = { onTipo(t) },
                            label = { Text(t.etiqueta) }
                        )
                    }
                }
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = onCantidad,
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = trabajador,
                    onValueChange = onTrabajador,
                    label = { Text("Nombre del trabajador") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                HorizontalDivider()
                Text(
                    "La fecha y hora se registran automaticamente.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmar, enabled = !procesando) {
                Text(if (procesando) "Guardando..." else "Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) { Text("Cancelar") }
        }
    )
}
