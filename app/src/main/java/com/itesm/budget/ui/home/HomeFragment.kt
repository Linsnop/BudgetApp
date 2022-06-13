package com.itesm.budget.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.itesm.budget.DatosUsuario
import com.itesm.budget.PantallaLogin
import com.itesm.budget.R
import com.itesm.budget.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSalir.setOnClickListener {
            //LogOut
            AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener {
                activity?.finish() //Cierra la actividad
                //Limpiar shared prefs
                val sharedPref = activity?.getSharedPreferences(
                    "usuario", AppCompatActivity.MODE_PRIVATE
                )
                sharedPref?.edit()?.clear()
                sharedPref?.edit()?.commit()


                // Cargar pantalla Login
                val intLogin = Intent(requireContext(), PantallaLogin::class.java)
                startActivity(intLogin)
            }
        }



        ////////Buscar Saldo
        BuscarSaldoEnNube()

        //Actualizar saldo boton
        binding.btnSaldo.setOnClickListener {
            if (binding.etSaldo.text.isNullOrEmpty())
                Toast.makeText(activity, "Campo 'Nuevo Saldo' Vacio",Toast.LENGTH_LONG).show()
            else
                DialogoNuevoSaldo()
        }

    }

    private fun ActualizarSaldo() {
        //Manda el saldo a firebase
        val nuevoSaldo = binding.etSaldo.text.toString().toFloat()

        val baseDatos = Firebase.database
        /// Obtener shared pref
        val sharedPref = activity?.getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val token = sharedPref?.getString("Token", "No existe")
        val nombre = sharedPref?.getString("Nombre", "No hay nombre")
        val correo = sharedPref?.getString("Correo","No hay Correo")

        val Usuario = DatosUsuario(token!!,nombre!!,correo!!,nuevoSaldo!!)

        val referencia = baseDatos.getReference("/Usuario/${token}")

        referencia.setValue(Usuario)

        //Actualizar shared pref
        val editor = sharedPref.edit()
        editor.putFloat("Saldo", nuevoSaldo.toFloat())
        editor.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun BuscarSaldoEnNube() {

        //Obtener shared Preferences
        val sharedPref = activity?.getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val saldo = sharedPref?.getFloat("Saldo", 0.0f)
        binding.tvSaldo.setText("Saldo Actual: ${saldo}")

    }

    /// Todos los dialogos

    private fun DialogoNuevoSaldo(){
        AlertDialog.Builder(activity) .apply {
            setTitle("Listo!!")
            setMessage("Saldo Actualizado")
            setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                //Accion positiva
                ActualizarSaldo()

            }
        }.show()
    }


}


