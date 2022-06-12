package com.itesm.budget

import com.itesm.budget.ui.Gastos.RegistroGasto

data class DatosUsuario(
    var token: String = "",
    var nombre: String = "",
    var correo: String = "",
    var saldo: Double = 0.0
    //var gastos: RegistroGasto = RegistroGasto("","",0.0,"")
)
