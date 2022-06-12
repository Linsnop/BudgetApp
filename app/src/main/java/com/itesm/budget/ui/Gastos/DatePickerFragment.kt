package com.itesm.budget.ui.Gastos

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val listener: (day:Int, month: Int, year: Int) -> Unit): DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        listener (day, month, year)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendario = Calendar.getInstance()
        val day = calendario.get(Calendar.DAY_OF_MONTH)
        val month = calendario.get(Calendar.MONTH)
        val year = calendario.get(Calendar.YEAR)

        val picker = DatePickerDialog(activity as Context, this, year,month,day)
        return picker
    }
}