package com.tecsup.stockmin.data.repository

import com.tecsup.stockmin.data.local.entity.ItemEntity
import com.tecsup.stockmin.data.local.entity.MovimientoEntity
import com.tecsup.stockmin.domain.model.Categoria
import com.tecsup.stockmin.domain.model.Item
import com.tecsup.stockmin.domain.model.Movimiento
import com.tecsup.stockmin.domain.model.TipoMovimiento

internal fun ItemEntity.toDomain(): Item = Item(
    id = id,
    nombre = nombre,
    categoria = Categoria.desdeEtiqueta(categoria),
    stockActual = stockActual,
    stockMinimo = stockMinimo
)

internal fun Item.toEntity(): ItemEntity = ItemEntity(
    id = id,
    nombre = nombre,
    categoria = categoria.etiqueta,
    stockActual = stockActual,
    stockMinimo = stockMinimo
)

internal fun MovimientoEntity.toDomain(): Movimiento = Movimiento(
    id = id,
    itemId = itemId,
    tipo = TipoMovimiento.desdeNombre(tipo),
    cantidad = cantidad,
    trabajador = trabajador,
    fechaMillis = fechaMillis
)

internal fun Movimiento.toEntity(): MovimientoEntity = MovimientoEntity(
    id = id,
    itemId = itemId,
    tipo = tipo.name,
    cantidad = cantidad,
    trabajador = trabajador,
    fechaMillis = fechaMillis
)
