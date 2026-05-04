package com.tecsup.stockmin.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tecsup.stockmin.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun buscarPorNombre(query: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun observarPorId(id: Long): Flow<ItemEntity?>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): ItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: ItemEntity): Long

    @Update
    suspend fun actualizar(item: ItemEntity)

    @Delete
    suspend fun eliminar(item: ItemEntity)

    /**
     * Actualiza el stock de un item de forma atomica.
     * Util al registrar movimientos.
     */
    @Query("UPDATE items SET stockActual = :nuevoStock WHERE id = :id")
    suspend fun actualizarStock(id: Long, nuevoStock: Int)
}
