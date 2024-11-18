package com.example.aplicacionalarcos

import android.app.DatePickerDialog
import java.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityDatosUsuarioBinding
import com.example.aplicacionalarcos.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker

class DatosUsuario : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // El icono del calendario es un boton para mostrar la fecha
        binding.etFechaNacimiento.setStartIconOnClickListener {
            showDatePickerDialog()
        }
    }

    // Mostrar el selector de fechas
    private fun showDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona una Fecha")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        // Mostrar el selector de fecha
        datePicker.show(supportFragmentManager, "datePicker")

        // Obtener la selección de la fecha y ponerla en el EditText
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = it?.let { date ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                // Formatear la fecha y ponerla en el TextInputEditText
                "$day / $month / $year"
            }

            // Actualizar el TextInputEditText con la fecha seleccionada
            binding.etFechaNacimiento.editText?.setText(selectedDate)
        }
    }
}