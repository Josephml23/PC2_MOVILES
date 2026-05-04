package com.tecsup.stockmin.data.repository

import com.tecsup.stockmin.data.local.dao.ItemDao
import com.tecsup.stockmin.data.local.dao.MovimientoDao
import com.tecsup.stockmin.domain.model.Item
import com.tecsup.stockmin.domain.model.Movimiento
import com.tecsup.stockmin.domain.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Punto unico de acceso a datos.
 * La UI nunca toca DAOs ni entidades directamente: solo modelos de dominio.
 */
class StockRepository(
    private val itemDao: ItemDao,
    private val movimientoDao: MovimientoDao
) {

    // -------- Items --------

    fun observarItems(filtro: String = ""): Flow<List<Item>> {
        val origen = if (filtro.isBlank()) {
            itemDao.observarTodos()
        } else {
            itemDao.buscarPorNombre(filtro.trim())
        }
        return origen.map { lista -> lista.map { it.toDomain() } }
    }

    fun observarItem(id: Long): Flow<Item?> =
        itemDao.observarPorId(id).map { it?.toDomain() }

    suspend fun guardarItem(item: Item): Long {
        return if (item.id == 0L) {
            itemDao.insertar(item.toEntity())
        } else {
            itemDao.actualizar(item.toEntity())
            item.id
        }
    }

    suspend fun eliminarItem(item: Item) {
        itemDao.eliminar(item.toEntity())
    }

    // -------- Movimientos --------

    fun observarMovimientos(itemId: Long): Flow<List<Movimiento>> =
        movimientoDao.observarPorItem(itemId).map { lista -> lista.map { it.toDomain() } }

    /**
     * Registra un movimiento y actualiza el stock en una unica operacion logica.
     * Devuelve Result para que la capa superior maneje errores (ej. stock insuficiente).
     */
    suspend fun registrarMovimiento(
        itemId: Long,
        tipo: TipoMovimiento,
        cantidad: Int,
        trabajador: String
    ): Result<Unit> {
        if (cantidad <= 0) {
            return Result.failure(IllegalArgumentException("La cantidad debe ser mayor a 0"))
        }
        if (trabajador.isBlank()) {
            return Result.failure(IllegalArgumentException("Falta el nombre del trabajador"))
        }

        val item = itemDao.obtenerPorId(itemId)
            ?: return Result.failure(IllegalStateException("El item ya no existe"))

        val nuevoStock = when (tipo) {
            TipoMovimiento.SALIDA -> item.stockActual - cantidad
            TipoMovimiento.ENTRADA -> item.stockActual + cantidad
        }

        if (nuevoStock < 0) {
            return Result.failure(IllegalStateException("Stock insuficiente para ese prestamo"))
        }

        val movimiento = Movimiento(
            itemId = itemId,
            tipo = tipo,
            cantidad = cantidad,
            trabajador = trabajador.trim(),
            fechaMillis = System.currentTimeMillis()
        )
        movimientoDao.insertar(movimiento.toEntity())
        itemDao.actualizarStock(itemId, nuevoStock)
        return Result.success(Unit)
    }
}
