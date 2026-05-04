package com.tecsup.stockmin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tecsup.stockmin.data.local.entity.MovimientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(movimiento: MovimientoEntity): Long

    @Query("SELECT * FROM movimientos WHERE itemId = :itemId ORDER BY fechaMillis DESC")
    fun observarPorItem(itemId: Long): Flow<List<MovimientoEntity>>

    @Query("SELECT * FROM movimientos ORDER BY fechaMillis DESC")
    fun observarTodos(): Flow<List<MovimientoEntity>>
}
