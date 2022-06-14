package com.itesm.budget.ui.slideshow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.itesm.budget.R
import com.itesm.budget.ui.Gastos.RegistroGasto

//Adaptador para el cecicler view


class AdaptadorGasto (private val contexto: Context, var arrGastos: Array<RegistroGasto?>)
    : RecyclerView.Adapter<AdaptadorGasto.RenglonCompra>() {

    //Listener
    //var listener : ListenerRecycler? = null

    //Pide que se cree un 'Renglon', ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RenglonCompra {
        val vista = LayoutInflater.from(contexto).inflate(R.layout.renglon_compra , parent,false)
        return RenglonCompra(vista)
    }

    //Pide que se llene el renglon 'Holder' en 'Position' (indice)
    override fun onBindViewHolder(holder: RenglonCompra, position: Int) {
        val gasto= arrGastos[position]
        holder.set(gasto!!)
        /*
        //Listener
        holder.vistaRenglon.setOnClickListener{
            listener?.itemClicked(position)
        }

         */
    }

    //Regresa el numero de renglones que tendra el Recycler View
    override fun getItemCount(): Int {
        return arrGastos.size
    }

    //Clase con vista del renglon
    class RenglonCompra (var vistaRenglon: View) : RecyclerView.ViewHolder(vistaRenglon) {

        fun set (gasto : RegistroGasto){
            //TextView con el nombre pais
            vistaRenglon.findViewById<TextView>(R.id.tvCompra).text= gasto.compra
            vistaRenglon.findViewById<TextView>(R.id.tvCategoria).text= gasto.categoria
            vistaRenglon.findViewById<TextView>(R.id.tvGasto).text= "$ ${gasto.gasto.toString()}"
            vistaRenglon.findViewById<TextView>(R.id.tvFecha).text= gasto.fecha
        }

    }
}