package com.itesm.budget.ui.slideshow

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.itesm.budget.databinding.FragmentSlideshowBinding
import com.itesm.budget.ui.Gastos.RegistroGasto

class SlideshowFragment : Fragment() {

    //Adaptador del recycler viewa
    private lateinit var adaptador: AdaptadorGasto

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarRecycleView()
    }


    private fun configurarRecycleView() {
        //Descargar datos de Firebase
        /// Obtener shared pref
        val sharedPref = activity?.getSharedPreferences(
            "usuario", AppCompatActivity.MODE_PRIVATE
        )
        val token = sharedPref?.getString("Token", "No existe")

        val baseDatos = Firebase.database
        val referencia = baseDatos.getReference("/Gastos/${token}")

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var arrGastos = mutableListOf<RegistroGasto?>()
                for (registro in snapshot.children) {
                    val gasto = registro.getValue(RegistroGasto::class.java)
                    arrGastos.add(gasto)
                }
                /*
                val adaptador = ArrayAdapter(
                    requireContext(),
                    R.layout.simple_list_item_1,
                    arrGastos
                )
                binding.rvGastos.adapter = adaptador

                 */

                //DataSource
                //val arrGastos = arrayOf(RegistroGasto("nada", "ahorro", 20.0f, "3/2/22"))
                val layoutManager = LinearLayoutManager(requireContext())
                binding.rvGastos.layoutManager = layoutManager

                val divisor = DividerItemDecoration(requireContext(), layoutManager.orientation)
                binding.rvGastos.addItemDecoration(divisor)

                adaptador = AdaptadorGasto(requireContext(), arrGastos.toTypedArray())
                binding.rvGastos.adapter = adaptador
            }

            override fun onCancelled(error: DatabaseError) {
                print("Error: $error")
            }
        })



        //adaptador.listener =this
    }

    /*
        private fun descargarDatosNube() {
            /// Obtener shared pref
            val sharedPref = activity?.getSharedPreferences(
                "usuario", AppCompatActivity.MODE_PRIVATE
            )
            val token = sharedPref?.getString("Token", "No existe")

            val baseDatos = Firebase.database
            val referencia = baseDatos.getReference("/Gastos/${token}")

            referencia.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var arrGastos = mutableListOf<String>()
                    for (registro in snapshot.children){
                        val gasto = registro.getValue(RegistroGasto::class.java)
                        arrGastos.add("${gasto?.categoria} - ${gasto?.gasto}")
                    }

                    val adaptador = ArrayAdapter(requireContext(),
                    android.R.layout.simple_list_item_1,
                    arrGastos)
                    binding.lvGastos.adapter = adaptador
                }

                override fun onCancelled(error: DatabaseError) {
                    print("Error: $error")
                }
            })
        }
    */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}