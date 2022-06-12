package com.itesm.budget.ui.Gastos

data class RegistroGasto(var compra : String = "",
                         var categoria: String="",
                         var gasto: Float = 0.0f,
                         var fecha: String = "")
