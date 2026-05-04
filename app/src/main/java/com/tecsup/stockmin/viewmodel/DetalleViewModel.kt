package com.tecsup.stockmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmin.data.repository.StockRepository
import com.tecsup.stockmin.domain.model.Item
import com.tecsup.stockmin.domain.model.Movimiento
import com.tecsup.stockmin.domain.model.TipoMovimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleUiState(
    val item: Item? = null,
    val historial: List<Movimiento> = emptyList(),
    val mostrarDialogoMovimiento: Boolean = false,
    val tipoSeleccionado: TipoMovimiento = TipoMovimiento.SALIDA,
    val cantidad: String = "1",
    val trabajador: String = "",
    val procesando: Boolean = false,
    val mensaje: String? = null,
    val itemEliminado: Boolean = false
)

sealed interface DetalleEvento {
    data object AbrirDialogo : DetalleEvento
    data object CerrarDialogo : DetalleEvento
    data class CambiarTipo(val v: TipoMovimiento) : DetalleEvento
    data class CambiarCantidad(val v: String) : DetalleEvento
    data class CambiarTrabajador(val v: String) : DetalleEvento
    data object ConfirmarMovimiento : DetalleEvento
    data object LimpiarMensaje : DetalleEvento
    data object EliminarItem : DetalleEvento
}

class DetalleViewModel(
    private val repository: StockRepository,
    private val itemId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUiState())

    /**
     * Combinamos el item observado y su historial para alimentar un unico StateFlow.
     * Cualquier cambio en BD (incluyendo el stock tras un movimiento) recorre
     * Repository -> Flow -> StateFlow -> UI sin que esta tenga que recargar nada.
     */
    val uiState: StateFlow<DetalleUiState> = combine(
        repository.observarItem(itemId),
        repository.observarMovimientos(itemId),
        _uiState
    ) { item, historial, base ->
        base.copy(item = item, historial = historial)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetalleUiState()
    )

    fun onEvento(evento: DetalleEvento) {
        when (evento) {
            DetalleEvento.AbrirDialogo -> _uiState.update {
                it.copy(
                    mostrarDialogoMovimiento = true,
                    cantidad = "1",
                    trabajador = "",
                    tipoSeleccionado = TipoMovimiento.SALIDA
                )
            }
            DetalleEvento.CerrarDialogo -> _uiState.update {
                it.copy(mostrarDialogoMovimiento = false)
            }
            is DetalleEvento.CambiarTipo -> _uiState.update {
                it.copy(tipoSeleccionado = evento.v)
            }
            is DetalleEvento.CambiarCantidad -> _uiState.update {
                it.copy(cantidad = evento.v.filter { c -> c.isDigit() })
            }
            is DetalleEvento.CambiarTrabajador -> _uiState.update {
                it.copy(trabajador = evento.v)
            }
            DetalleEvento.LimpiarMensaje -> _uiState.update { it.copy(mensaje = null) }
            DetalleEvento.ConfirmarMovimiento -> registrarMovimiento()
            DetalleEvento.EliminarItem -> eliminar()
        }
    }

    private fun registrarMovimiento() {
        val state = _uiState.value
        val cantidad = state.cantidad.toIntOrNull() ?: 0

        viewModelScope.launch {
            _uiState.update { it.copy(procesando = true) }
            val resultado = repository.registrarMovimiento(
                itemId = itemId,
                tipo = state.tipoSeleccionado,
                cantidad = cantidad,
                trabajador = state.trabajador
            )
            _uiState.update {
                it.copy(
                    procesando = false,
                    mostrarDialogoMovimiento = resultado.isFailure,
                    mensaje = resultado.fold(
                        onSuccess = { "Movimiento registrado" },
                        onFailure = { e -> e.message ?: "Error al registrar" }
                    )
                )
            }
        }
    }

    private fun eliminar() {
        val item = _uiState.value.item ?: return
        viewModelScope.launch {
            repository.eliminarItem(item)
            _uiState.update { it.copy(itemEliminado = true) }
        }
    }
}
