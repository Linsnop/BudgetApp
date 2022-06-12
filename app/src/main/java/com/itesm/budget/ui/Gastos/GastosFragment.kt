package com.itesm.budget.ui.Gastos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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

        /// Fecha
        binding.etFecha.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnGuardarGasto.setOnClickListener{
            guardarNube()
        }
        binding.btnRegresar.setOnClickListener{
            regresar()
        }
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
        val categoria = binding.spCategoria.selectedItem.toString()
        val gasto = binding.etGasto.text.toString().toDouble()
        val fecha = binding.etFecha.text.toString()

        val registroGasto = RegistroGasto(categoria,gasto,fecha)

        val referencia = baseDatos.getReference("/RegistroGasto/$categoria")

        referencia.setValue(registroGasto)

        println("Dato guardado en la nube")
    }

    private fun configurarLista() {
        val arrCategorias = listOf("Ahorro","Gastos fijos","Entretenimiento", "Servicios", "Despensa")
        val adaptador = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,arrCategorias)
        binding.spCategoria.adapter = adaptador
    }
}