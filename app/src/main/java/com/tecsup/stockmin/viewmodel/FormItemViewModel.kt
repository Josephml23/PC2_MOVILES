package com.tecsup.stockmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmin.data.repository.StockRepository
import com.tecsup.stockmin.domain.model.Categoria
import com.tecsup.stockmin.domain.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormItemUiState(
    val id: Long = 0L,
    val nombre: String = "",
    val categoria: Categoria = Categoria.HERRAMIENTA,
    val stockActual: String = "",
    val stockMinimo: String = "",
    val cargando: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null
) {
    val esEdicion: Boolean get() = id != 0L

    val nombreValido: Boolean get() = nombre.isNotBlank()
    val stockActualValido: Boolean get() = stockActual.toIntOrNull()?.let { it >= 0 } ?: false
    val stockMinimoValido: Boolean get() = stockMinimo.toIntOrNull()?.let { it >= 0 } ?: false
    val formularioValido: Boolean
        get() = nombreValido && stockActualValido && stockMinimoValido
}

sealed interface FormItemEvento {
    data class CambiarNombre(val v: String) : FormItemEvento
    data class CambiarCategoria(val v: Categoria) : FormItemEvento
    data class CambiarStockActual(val v: String) : FormItemEvento
    data class CambiarStockMinimo(val v: String) : FormItemEvento
    data object Guardar : FormItemEvento
    data object LimpiarError : FormItemEvento
}

class FormItemViewModel(
    private val repository: StockRepository,
    private val itemId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormItemUiState())
    val uiState: StateFlow<FormItemUiState> = _uiState.asStateFlow()

    init {
        if (itemId != 0L) cargarItem(itemId)
    }

    private fun cargarItem(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            val item = repository.observarItem(id).first()
            if (item != null) {
                _uiState.update {
                    it.copy(
                        id = item.id,
                        nombre = item.nombre,
                        categoria = item.categoria,
                        stockActual = item.stockActual.toString(),
                        stockMinimo = item.stockMinimo.toString(),
                        cargando = false
                    )
                }
            } else {
                _uiState.update { it.copy(cargando = false, error = "Item no encontrado") }
            }
        }
    }

    fun onEvento(evento: FormItemEvento) {
        when (evento) {
            is FormItemEvento.CambiarNombre -> _uiState.update { it.copy(nombre = evento.v) }
            is FormItemEvento.CambiarCategoria -> _uiState.update { it.copy(categoria = evento.v) }
            is FormItemEvento.CambiarStockActual ->
                _uiState.update { it.copy(stockActual = evento.v.filter { c -> c.isDigit() }) }
            is FormItemEvento.CambiarStockMinimo ->
                _uiState.update { it.copy(stockMinimo = evento.v.filter { c -> c.isDigit() }) }
            FormItemEvento.LimpiarError -> _uiState.update { it.copy(error = null) }
            FormItemEvento.Guardar -> guardar()
        }
    }

    private fun guardar() {
        val state = _uiState.value
        if (!state.formularioValido) {
            _uiState.update { it.copy(error = "Completa los campos obligatorios") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val item = Item(
                    id = state.id,
                    nombre = state.nombre.trim(),
                    categoria = state.categoria,
                    stockActual = state.stockActual.toInt(),
                    stockMinimo = state.stockMinimo.toInt()
                )
                repository.guardarItem(item)
                _uiState.update { it.copy(cargando = false, guardado = true) }
            } catch (t: Throwable) {
                _uiState.update { it.copy(cargando = false, error = t.message ?: "Error al guardar") }
            }
        }
    }
}
