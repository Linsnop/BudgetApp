package com.itesm.budget

data class DatosUsuario(
    var token: String = "",
    var nombre: String = "",
    var correo: String = "",
    var saldo: Double = 0.0
)