package com.example.aplicacionalarcos

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import objetos.UserSession
import java.util.*

class InformacionNutricionalActivity : AppCompatActivity() {

    private lateinit var tvFecha: TextView
    private lateinit var tvCalorias: TextView
    private lateinit var tvProteinas: TextView
    private lateinit var tvGrasas: TextView
    private lateinit var tvCarbohidratos: TextView
    private lateinit var btnSeleccionarFecha: Button
    private lateinit var obAtras2: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iformacion_nutricional)

        tvFecha = findViewById(R.id.tvFecha)
        tvCalorias = findViewById(R.id.tvCalorias)
        tvProteinas = findViewById(R.id.tvProteinas)
        tvGrasas = findViewById(R.id.tvGrasas)
        tvCarbohidratos = findViewById(R.id.tvCarbohidratos)
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha)
        obAtras2 = findViewById(R.id.obAtras2)

        btnSeleccionarFecha.setOnClickListener {
            seleccionarFecha()
        }

        // Configuración del botón "Atrás"
        obAtras2.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    private fun seleccionarFecha() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
            tvFecha.text = "Fecha seleccionada: $fechaSeleccionada"
            obtenerDatosPorFecha(fechaSeleccionada)
        }, year, month, day)

        datePicker.show()
    }

    private fun obtenerDatosPorFecha(fecha: String) {
        try {
            val userId = UserSession.id
            if (userId.isNullOrEmpty()) {
                Toast.makeText(this, "Error: No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show()
                return
            }

            // Convertir "dd/MM/yyyy" a "yyyy-MM-dd"
            val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatoSalida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val fechaConvertida = formatoSalida.format(formatoEntrada.parse(fecha)!!)

            db.collection("platos")
                .whereEqualTo("id", userId) // Filtrar por el usuario actual
                .whereEqualTo("fechaRegistro", fechaConvertida) // Filtrar por la fecha seleccionada
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        tvCalorias.text = "Calorías totales: 0 kcal"
                        tvProteinas.text = "Proteínas totales: 0 g"
                        tvGrasas.text = "Grasas totales: 0 g"
                        tvCarbohidratos.text = "Carbohidratos totales: 0 g"
                        Toast.makeText(this, "No hay datos para esta fecha", Toast.LENGTH_SHORT).show()
                    } else {
                        var totalCalorias = 0.0
                        var totalProteinas = 0.0
                        var totalGrasas = 0.0
                        var totalCarbohidratos = 0.0

                        for (document in documents) {
                            totalCalorias += document.getDouble("valorEnergeticoTotal") ?: 0.0
                            totalProteinas += document.getDouble("proteinasTotales") ?: 0.0
                            totalGrasas += document.getDouble("grasasTotales") ?: 0.0
                            totalCarbohidratos += document.getDouble("carbohidratosTotales") ?: 0.0
                        }

                        tvCalorias.text = "Calorías totales: ${totalCalorias.toInt()} kcal"
                        tvProteinas.text = "Proteínas totales: %.2f g".format(totalProteinas)
                        tvGrasas.text = "Grasas totales: %.2f g".format(totalGrasas)
                        tvCarbohidratos.text = "Carbohidratos totales: %.2f g".format(totalCarbohidratos)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show()
        }
    }
}
