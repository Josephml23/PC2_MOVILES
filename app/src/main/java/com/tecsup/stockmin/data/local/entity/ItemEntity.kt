package com.tecsup.stockmin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un item del almacen: herramienta, insumo o EPP.
 */
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val categoria: String,   // "Herramienta", "Insumo", "EPP"
    val stockActual: Int,
    val stockMinimo: Int
)
