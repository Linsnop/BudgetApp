package com.itesm.budget.ui.Gastos

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.itesm.budget.DatosUsuario
import com.itesm.budget.MainActivity

import com.itesm.budget.databinding.FragmentGastosBinding

class GastosFragment : Fragment() {

    private lateinit var baseDatos: FirebaseDatabase

    // Binding
    private lateinit var binding: FragmentGastosBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGastosBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseDatos = Firebase.database
        // Registrar eventos
        configurarLista()
        // Verificar Saldo
            ObtenerSaldo()

        /// Fecha
        binding.etFecha.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnGuardarGasto.setOnClickListener{
            //Obtener shared Preferences
            val sharedPref = activity?.getSharedPreferences(
                "usuario", AppCompatActivity.MODE_PRIVATE
            )
            val saldo = sharedPref?.getFloat("Saldo", 0.0f)


            if (binding.etCompra.text.isNullOrEmpty())
                Toast.makeText(activity, "Campo '¿Qué se compró?' Vacio",Toast.LENGTH_LONG).show()
            else
                if (binding.etGasto.text.isEmpty())

                    Toast.makeText(activity, "Campo 'Gasto' Vacio",Toast.LENGTH_LONG).show()

                else
                    if (binding.etFecha.text.isEmpty())
                        Toast.makeText(activity, "Campo 'Fecha' Vacio",Toast.LENGTH_LONG).show()
                    else
                        if ( (saldo!! - binding.etGasto.text.toString().toFloat()) < 0.0f )
                            DialogoError()
                            else
                                actualizarSaldo()
                                //DialogoAñadir()
        }
        binding.btnRegresar.setOnClickListener{
            regresar()
        }


    }

    private fun ObtenerSaldo() {
        //Obtener shared Preferences
        val sharedPref = activity?.getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val saldo = sharedPref?.getFloat("Saldo", 0.0f)

        binding.tvSaldoGastos.text = saldo.toString()

            }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment{day,month,year -> onDateSelected(day,month,year)}
        datePicker.show(parentFragmentManager,"date")
    }

    fun onDateSelected(day:Int, month: Int, year:Int) {
        binding.etFecha.setText("$day/$month/$year")

    }

    private fun regresar() {
        val intRegresar = Intent (requireContext(), MainActivity::class.java)
        startActivity(intRegresar)
    }

    private fun guardarNube() {
        val baseDatos = Firebase.database
        val compra = binding.etCompra.text.toString()
        val categoria = binding.spCategoria.selectedItem.toString()
        val gasto = binding.etGasto.text.toString().toFloat()
        val fecha = binding.etFecha.text.toString()


        //Shared Pref
        val sharedPref = activity?.getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val token = sharedPref?.getString("Token", "No existe")

        val registroGasto = RegistroGasto(compra,categoria,gasto,fecha)

        val referencia = baseDatos.getReference("/Gastos/${token}/${compra}")
        //val referencia = baseDatos.getReference("/Usuario/${token}/${}")

        referencia.setValue(registroGasto)

        println("Dato guardado en la nube")
        //actualizarSaldo()
        //regresar()
    }

    private fun configurarLista() {
        val arrCategorias = listOf("Ahorro","Gastos fijos","Entretenimiento", "Servicios", "Despensa","Otro")
        val adaptador = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,arrCategorias)
        binding.spCategoria.adapter = adaptador
    }

    private fun actualizarSaldo() {
        /// Obtener shared pref
        val sharedPref = activity?.getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val token = sharedPref?.getString("Token", "No existe")
        val nombre = sharedPref?.getString("Nombre", "No hay nombre")
        val correo = sharedPref?.getString("Correo","No hay Correo")
        val saldo = sharedPref?.getFloat("Saldo",0.0f)

        val nuevoSaldo = (saldo!! - binding.etGasto.text.toString().toFloat())

        val Usuario = DatosUsuario(token!!,nombre!!,correo!!,nuevoSaldo!!)

        val referencia = baseDatos.getReference("/Usuario/${token}")

        referencia.setValue(Usuario)

        //Actualizar shared pref
        val editor = sharedPref.edit()
        editor.putFloat("Saldo", nuevoSaldo.toFloat())
        editor.commit()

        println("Usuario actualizado")

        DialogoAñadir()
    }

    /// Todos los dialogos

    private fun DialogoAñadir(){
        AlertDialog.Builder(activity) .apply {
            setTitle("Añadir Gasto")
            setMessage("Gasto añadido !! Regresando a pantalla principal")
            setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                //Accion positiva
                guardarNube()

            }
        }.show()
    }



    private fun DialogoError(){
        AlertDialog.Builder(activity) .apply {
            setTitle("Error")
            setMessage("El costo del gasto no puede ser mayor a tu saldo actual")
            setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                //Accion

            }
        }.show()
    }

}