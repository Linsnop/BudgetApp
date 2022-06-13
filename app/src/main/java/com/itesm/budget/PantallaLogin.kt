package com.itesm.budget

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.itesm.budget.databinding.ActivityPantallaLoginBinding

class PantallaLogin : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_pantalla_login)


        //Registrar evento
        binding.btnLogin.setOnClickListener {
            autenticar()
        }

        verificarLogin()
    }

    // Si ya esta firmado, Pasa a la segunda pantalla
    private fun verificarLogin() {

        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario != null) {
            //Ya esta firmado
            println("Bienvenido ***** ${usuario.displayName} ****")
            //Guardar los datos en el editor

            val sharedPref = getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString("Token", usuario?.uid)
            editor.putString("Nombre", usuario?.displayName)
            editor.putString("Correo", usuario?.email)
            editor.commit()
            //Intento de conseguir saldo

            BuscarSaldoEnNube()

            /// Subir a la base de datos
            guardarDatosNube()
            entrarAPP()
        }
    }

    private fun BuscarSaldoEnNube() {

        //Obtener shared Preferences
        val sharedPref = getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val token = sharedPref?.getString(
            "Token", "No existe")

            val baseDatos = Firebase . database
        val referencia = baseDatos.getReference("/Usuario/${token}")

        referencia.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //llegaron los datos (Snapshot)
                val usuario = snapshot.getValue(DatosUsuario::class.java)
                val saldoActual = usuario!!.saldo

                /// Guadrar saldo en editor
                val editor = sharedPref.edit()
                editor.putFloat("Saldo", saldoActual)
                editor.commit()
            }

            override fun onCancelled(error: DatabaseError) {
                print("Error: $error")
            }

        })


    }

    private fun guardarDatosNube() {
        val baseDatos = Firebase.database
        /// Obtener el token del usuario
        val sharedPref = getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val token = sharedPref?.getString("Token", "No existe")
        val nombre = sharedPref?.getString("Nombre", "No hay nombre")
        val correo = sharedPref?.getString("Correo", "No hay Correo")
        val saldo = sharedPref?.getFloat("Saldo", 0.0f)

        println("El saldo en home es ${saldo}")

        val Usuario = DatosUsuario(token!!, nombre!!, correo!!, saldo!!)

        val referencia = baseDatos.getReference("/Usuario/${token}")

        referencia.setValue(Usuario)

        println("Dato guardado en la nube")
    }

    private fun entrarAPP() {
        // Entrar a la app
        val intApp = Intent(this, MainActivity::class.java)
        startActivity(intApp)
        //Borrar pantalla de Login
        finish() // Descarga de memoria la actividad

    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val usuario = FirebaseAuth.getInstance().currentUser
            println("Bienvenido: ${usuario?.displayName}")
            println("Correo: ${usuario?.email}")
            println("Token: ${usuario?.uid}")
            // Intento de guardar token de usuario
            val sharedPref = getSharedPreferences(
                "usuario", MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString("Token", usuario?.uid)
            editor.commit()

            entrarAPP()
        } else {
            //Sign In Failed xD
            println("Error: Intantalo de nuevo")
        }

    }

    private fun autenticar() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }
}