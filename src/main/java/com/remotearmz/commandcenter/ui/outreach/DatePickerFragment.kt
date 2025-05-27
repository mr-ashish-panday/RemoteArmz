package com.remotearmz.commandcenter.ui.outreach

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatePickerFragment(private val onDateSelected: (String) -> Unit) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val current = LocalDate.now()
        val picker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = LocalDate.of(year, month + 1, day)
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                onDateSelected(selectedDate.format(formatter))
            },
            current.year,
            current.monthValue - 1,
            current.dayOfMonth
        )
        picker.show()
        return null
    }
}
