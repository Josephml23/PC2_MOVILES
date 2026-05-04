package com.tecsup.stockmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.stockmin.data.repository.StockRepository
import com.tecsup.stockmin.domain.model.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Estado unico que la UI consume (UDF).
 */
data class CatalogoUiState(
    val items: List<Item> = emptyList(),
    val filtro: String = "",
    val cargando: Boolean = true
) {
    val itemsBajoStock: Int get() = items.count { it.bajoStock }
}

/**
 * Eventos que la UI envia al ViewModel (single source of truth -> ViewModel maneja la logica).
 */
sealed interface CatalogoEvento {
    data class CambiarFiltro(val texto: String) : CatalogoEvento
}

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModel(
    private val repository: StockRepository
) : ViewModel() {

    private val filtro = MutableStateFlow("")

    private val itemsFlow = filtro.flatMapLatest { q ->
        repository.observarItems(q)
    }

    val uiState: StateFlow<CatalogoUiState> =
        combine(itemsFlow, filtro) { items, q ->
            CatalogoUiState(items = items, filtro = q, cargando = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CatalogoUiState()
        )

    fun onEvento(evento: CatalogoEvento) {
        when (evento) {
            is CatalogoEvento.CambiarFiltro -> filtro.update { evento.texto }
        }
    }
}
