package com.itesm.budget

import com.itesm.budget.ui.Gastos.RegistroGasto

data class DatosUsuario(
    var token: String = "",
    var nombre: String = "",
    var correo: String = "",
    var saldo: Float = 0.0f
    //var gastos: RegistroGasto = RegistroGasto("","",0.0,"")
)
