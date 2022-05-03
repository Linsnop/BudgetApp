package com.itesm.budget.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.itesm.budget.databinding.FragmentSlideshowBinding
import com.itesm.budget.ui.gallery.RegistroGasto

class SlideshowFragment : Fragment() {

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

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        descargarDatosNube()
    }

    private fun descargarDatosNube() {
        val baseDatos = Firebase.database
        val referencia = baseDatos.getReference("/RegistroGasto")

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}