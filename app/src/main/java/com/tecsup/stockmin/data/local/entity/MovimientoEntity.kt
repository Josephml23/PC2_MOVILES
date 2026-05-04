package com.tecsup.stockmin.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registra un movimiento (salida = prestamo, entrada = devolucion) sobre un item.
 * Se vincula al item mediante itemId con eliminacion en cascada.
 */
@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["itemId"])]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val tipo: String,           // "SALIDA" o "ENTRADA"
    val cantidad: Int,
    val trabajador: String,
    val fechaMillis: Long       // timestamp automatico
)
