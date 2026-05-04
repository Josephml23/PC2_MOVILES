package com.tecsup.stockmin

import android.app.Application
import com.tecsup.stockmin.data.local.StockMinDatabase
import com.tecsup.stockmin.data.repository.StockRepository

/**
 * Application root. Expone un Service Locator manual para evitar
 * la complejidad de Hilt en un proyecto academico.
 */
class StockMinApp : Application() {

    val database: StockMinDatabase by lazy { StockMinDatabase.getInstance(this) }

    val repository: StockRepository by lazy {
        StockRepository(
            itemDao = database.itemDao(),
            movimientoDao = database.movimientoDao()
        )
    }
}
