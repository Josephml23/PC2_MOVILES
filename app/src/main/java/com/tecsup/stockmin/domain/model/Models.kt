package com.tecsup.stockmin.domain.model

/**
 * Modelo de dominio de un item, desacoplado de Room.
 */
data class Item(
    val id: Long = 0,
    val nombre: String,
    val categoria: Categoria,
    val stockActual: Int,
    val stockMinimo: Int
) {
    val bajoStock: Boolean get() = stockActual < stockMinimo
}

enum class Categoria(val etiqueta: String) {
    HERRAMIENTA("Herramienta"),
    INSUMO("Insumo"),
    EPP("EPP");

    companion object {
        fun desdeEtiqueta(etiqueta: String): Categoria =
            values().firstOrNull { it.etiqueta == etiqueta } ?: HERRAMIENTA
    }
}

data class Movimiento(
    val id: Long = 0,
    val itemId: Long,
    val tipo: TipoMovimiento,
    val cantidad: Int,
    val trabajador: String,
    val fechaMillis: Long
)

enum class TipoMovimiento(val etiqueta: String) {
    SALIDA("Salida (prestamo)"),
    ENTRADA("Entrada (devolucion)");

    companion object {
        fun desdeNombre(nombre: String): TipoMovimiento =
            values().firstOrNull { it.name == nombre } ?: SALIDA
    }
}
