package com.tecsup.stockmin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.stockmin.data.repository.StockRepository

/**
 * Factory unica para todos los ViewModels.
 * Mauricio: aqui evitamos Hilt para mantener el proyecto academico simple.
 */
class StockMinVMFactory(
    private val repository: StockRepository,
    private val itemId: Long = 0L
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CatalogoViewModel::class.java) ->
                CatalogoViewModel(repository) as T
            modelClass.isAssignableFrom(FormItemViewModel::class.java) ->
                FormItemViewModel(repository, itemId) as T
            modelClass.isAssignableFrom(DetalleViewModel::class.java) ->
                DetalleViewModel(repository, itemId) as T
            else -> throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
        }
    }
}
