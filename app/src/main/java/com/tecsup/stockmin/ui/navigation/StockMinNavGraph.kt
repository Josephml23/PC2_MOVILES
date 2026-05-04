package com.tecsup.stockmin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.stockmin.data.repository.StockRepository
import com.tecsup.stockmin.ui.screens.catalog.CatalogoScreen
import com.tecsup.stockmin.ui.screens.detail.DetalleScreen
import com.tecsup.stockmin.ui.screens.form.FormItemScreen
import com.tecsup.stockmin.viewmodel.CatalogoViewModel
import com.tecsup.stockmin.viewmodel.DetalleViewModel
import com.tecsup.stockmin.viewmodel.FormItemViewModel
import com.tecsup.stockmin.viewmodel.StockMinVMFactory

object Rutas {
    const val CATALOGO = "catalogo"
    const val FORM = "form/{itemId}"
    const val DETALLE = "detalle/{itemId}"

    fun form(itemId: Long = 0L) = "form/$itemId"
    fun detalle(itemId: Long) = "detalle/$itemId"
}

@Composable
fun StockMinNavGraph(repository: StockRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.CATALOGO) {

        composable(Rutas.CATALOGO) {
            val vm: CatalogoViewModel = viewModel(factory = StockMinVMFactory(repository))
            CatalogoScreen(
                viewModel = vm,
                onNuevoItem = { navController.navigate(Rutas.form()) },
                onAbrirItem = { id -> navController.navigate(Rutas.detalle(id)) }
            )
        }

        composable(
            route = Rutas.FORM,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStack ->
            val itemId = backStack.arguments?.getLong("itemId") ?: 0L
            val vm: FormItemViewModel = viewModel(
                factory = StockMinVMFactory(repository, itemId),
                key = "form-$itemId"
            )
            FormItemScreen(
                viewModel = vm,
                onAtras = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStack ->
            val itemId = backStack.arguments?.getLong("itemId") ?: 0L
            val vm: DetalleViewModel = viewModel(
                factory = StockMinVMFactory(repository, itemId),
                key = "detalle-$itemId"
            )
            DetalleScreen(
                viewModel = vm,
                onAtras = { navController.popBackStack() },
                onEditar = { navController.navigate(Rutas.form(itemId)) }
            )
        }
    }
}
