package com.example.aplicacionalarcos

import android.app.DatePickerDialog
import java.util.Calendar
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityDatosUsuarioBinding
import com.example.aplicacionalarcos.databinding.ActivityMainBinding

class DatosUsuario : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Asignar evento de clic al EditText
        binding.etFechaNacimiento.setOnClickListener {
            showDatePickerDialog()
        }
    }



    //Mostrar la fecha seleccionada en el EditText
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay / ${selectedMonth + 1} / $selectedYear"
                binding.etFechaNacimiento.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}