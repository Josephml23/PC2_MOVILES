package com.tecsup.stockmin.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Fechas {
    private val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PE"))
    fun formatear(millis: Long): String = formato.format(Date(millis))
}
