package com.itesm.budget

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.itesm.budget.databinding.ActivityPantallaLoginBinding

class PantallaLogin : AppCompatActivity() {

    private  lateinit var binding: ActivityPantallaLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_pantalla_login)


        //Registrar evento
        binding.btnLogin.setOnClickListener{
            autenticar()
        }

        verificarLogin()
    }

    // Si ya esta firmado, Pasa a la segunda pantalla
    private fun verificarLogin() {

        val usuario= FirebaseAuth.getInstance().currentUser
        if (usuario!=null){
            //Ya esta firmado
            println("Bienvenido ***** ${usuario.displayName} ****")
            //Guardar los datos en el editor

            val sharedPref = getSharedPreferences(
                "usuario", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("Token", usuario?.uid)
            editor.putString("Nombre",usuario?.displayName)
            editor.putString("Correo",usuario?.email)
            editor.commit()
            //Intento de conseguir saldo

            /// Subir a la base de datos
            guardarDatosNube()
            entrarAPP()
        }
    }

    private fun guardarDatosNube() {
        val baseDatos = Firebase.database
        /// Obtener el token del usuario
        val sharedPref = getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val token = sharedPref?.getString("Token", "No existe")
        val nombre = sharedPref?.getString("Nombre", "No hay nombre")
        val correo = sharedPref?.getString("Correo","No hay Correo")

        //println("El token en home es ${token}")

        val Usuario = DatosUsuario(token!!,nombre!!,correo!!)

        val referencia = baseDatos.getReference("/Usuario/${token}")

        referencia.setValue(Usuario)

        println("Dato guardado en la nube")
    }

    private fun entrarAPP() {
        // Entrar a la app
        val intApp= Intent(this, MainActivity::class.java)
        startActivity(intApp)
        //Borrar pantalla de Login
        finish() // Descarga de memoria la actividad

    }

    private val signInLauncher= registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ){
            res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode== RESULT_OK){
            val usuario= FirebaseAuth.getInstance().currentUser
            println("Bienvenido: ${usuario?.displayName}")
            println("Correo: ${usuario?.email}")
            println("Token: ${usuario?.uid}")
            // Intento de guardar token de usuario
            val sharedPref = getSharedPreferences(
                "usuario", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("Token", usuario?.uid)
            editor.commit()

            entrarAPP()
        } else{
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