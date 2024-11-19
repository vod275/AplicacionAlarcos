package com.example.aplicacionalarcos

import android.app.AlertDialog
import android.content.Intent
import java.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityDatosUsuarioBinding
import com.example.aplicacionalarcos.databinding.ActivityMensajeMotivacionalBinding
import com.google.android.material.datepicker.MaterialDatePicker

class DatosUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // El icono del calendario es un boton para mostrar la fecha
        binding.etFechaNacimiento.setStartIconOnClickListener {
            showDatePickerDialog()
        }

        //boton de aceptar, ya crearemos un metodo mas adelante
        binding.btnAcceptDialog.setOnClickListener {

            showMotivationalDialog()

        }

        //boton atras(sera generico asi no hacemos 500)
        binding.obAtras.setOnClickListener {

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

        // Obtener la fecha y ponerla en el EditText
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = it?.let { date ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH) + 1
                val year = calendar.get(Calendar.YEAR)
                // Formatear la fecha y ponerla en el TextInputEditText
                if (month<10){
                    "$day / 0$month / $year"
                }else{
                    "$day / $month / $year"
                }
            }

            // Actualizar el TextInputEditText con la fecha seleccionada
            binding.etFechaNacimiento.editText?.setText(selectedDate)
        }
    }

    private fun showMotivationalDialog() {
        // Vinculamos el diseño del cuadro de diálogo
        val dialogBinding = ActivityMensajeMotivacionalBinding.inflate(layoutInflater)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)

        // Configuramos la acción del botón "Aceptar"
        val dialog = dialogBuilder.create()
        dialogBinding.btnAcceptDialog.setOnClickListener {
            dialog.dismiss()
            navigateToImcActivity()
        }

        dialog.show()
    }

    private fun navigateToImcActivity() {
        val intent = Intent(this, ImcActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "¡A calcular el IMC!", Toast.LENGTH_SHORT).show()
    }
}