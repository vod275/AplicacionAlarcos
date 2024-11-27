package com.example.aplicacionalarcos

import android.content.Intent
import java.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityDatosUsuarioBinding
import com.google.android.material.datepicker.MaterialDatePicker
import objetos.UserSession

class DatosUsuarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatosUsuarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mostrar el email del usuario
        binding.tvCorreo.text = UserSession.nombre ?: getString(R.string.usuario_no_disponible)

        // El icono del calendario es un botón para mostrar la fecha
        binding.etFechaNacimiento.setStartIconOnClickListener {
            showDatePickerDialog()
        }

        // Botón atrás: cerrar sesión y volver al inicio
        binding.obAtras.setOnClickListener {
            val intent = Intent(this, AjustesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        binding.btnAceptar.setOnClickListener {
            // Obtener los valores de los campos de texto
            val nombre = binding.tvNombreAjustes.editText?.text.toString().takeIf { it.isNotEmpty() }
                ?: getString(R.string.nombre_no_disponible)
            val apellidos = binding.tvApellidos.editText?.text.toString().takeIf { it.isNotEmpty() }
                ?: getString(R.string.apellidos_no_disponibles)
            val fechaNacimiento =
                binding.etFechaNacimientoEditText.text.toString().takeIf { it.isNotEmpty() }
                    ?: getString(R.string.fecha_no_disponible)

            // Validar que los campos no estén vacíos antes de pasar los datos (opcional)
            if (nombre.isNotEmpty() && apellidos.isNotEmpty() && fechaNacimiento.isNotEmpty()) {
                // Crear un Intent para pasar los datos al siguiente Activity
                val intent = Intent(this, AjustesActivity::class.java)
                intent.putExtra("nombre", nombre)
                intent.putExtra("apellidos", apellidos)
                intent.putExtra("fechaNacimiento", fechaNacimiento)
                startActivity(intent)
            } else {
                // Mostrar un mensaje si algún campo está vacío
                Toast.makeText(this, getString(R.string.error_campos_vacios), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Función para la fecha
    private fun showDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.seleccionar_fecha))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTheme(R.style.ThemeOverlay_App_DatePicker) // Aplica el tema personalizado
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
                // Formato estándar para la fecha
                "$year-$month-$day"
            }

            // Actualizar el TextInputEditText con la fecha seleccionada
            binding.etFechaNacimiento.editText?.setText(selectedDate)
        }
    }
}
