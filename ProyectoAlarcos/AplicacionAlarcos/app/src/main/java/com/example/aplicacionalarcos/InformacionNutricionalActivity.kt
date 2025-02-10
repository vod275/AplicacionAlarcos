package com.example.aplicacionalarcos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import objetos.UserSession
import java.text.SimpleDateFormat
import java.util.*

class InformacionNutricionalActivity : AppCompatActivity() {

    private lateinit var tvFecha: TextView
    private lateinit var tvCalorias: TextView
    private lateinit var tvProteinas: TextView
    private lateinit var tvGrasas: TextView
    private lateinit var tvCarbohidratos: TextView
    private lateinit var btnSeleccionarFecha: Button
    private lateinit var obAtras2: Button
    private lateinit var spinnerFiltro: Spinner
    private lateinit var tvSal: TextView
    private val db = FirebaseFirestore.getInstance()
    private var opcionSeleccionada = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iformacion_nutricional)

        tvFecha = findViewById(R.id.tvFecha)
        tvCalorias = findViewById(R.id.tvCalorias)
        tvProteinas = findViewById(R.id.tvProteinas)
        tvGrasas = findViewById(R.id.tvGrasas)
        tvSal = findViewById(R.id.tvSal)
        tvCarbohidratos = findViewById(R.id.tvCarbohidratos)
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha)
        obAtras2 = findViewById(R.id.obAtras2)
        spinnerFiltro = findViewById(R.id.spinnerFiltro)

        // Configurar Spinner con las opciones de filtro
        val opciones = arrayOf(
            getString(R.string.opcion_dia),
            getString(R.string.opcion_semana),
            getString(R.string.opcion_mes)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFiltro.adapter = adapter

        // Listener del Spinner
        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                opcionSeleccionada = opciones[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSeleccionarFecha.setOnClickListener {
            seleccionarFecha()
        }

        obAtras2.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun seleccionarFecha() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
            getString(R.string.fecha_seleccionada, fechaSeleccionada).also { tvFecha.text = it }
            obtenerDatosPorFecha(fechaSeleccionada)
        }, year, month, day)

        datePicker.show()
    }

    private fun obtenerDatosPorFecha(fecha: String) {
        try {
            val userId = UserSession.id
            if (userId.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.error_usuario), Toast.LENGTH_SHORT).show()
                return
            }

            // Convertir "dd/MM/yyyy" a "yyyy-MM-dd"
            val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatoSalida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaConvertida = formatoSalida.format(formatoEntrada.parse(fecha)!!)

            val (fechaInicio, fechaFin) = obtenerRangoFechas(fechaConvertida, opcionSeleccionada)

            Log.d("Firestore", getString(R.string.log_busqueda_firebase, fechaInicio, fechaFin, userId))

            db.collection("platos")
                .whereEqualTo("id", userId)
                .whereGreaterThanOrEqualTo("fechaRegistro", fechaInicio)
                .whereLessThanOrEqualTo("fechaRegistro", fechaFin)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        mostrarDatosVacios()
                    } else {
                        procesarResultados(documents)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, getString(R.string.error_datos_firebase), Toast.LENGTH_SHORT).show()
                    Log.e("Firestore", getString(R.string.error_firebase), e)
                }
        } catch (e: Exception) {
            Log.e("Fecha", getString(R.string.error_conversion_fecha), e)
            Toast.makeText(this, getString(R.string.error_formato_fecha), Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerRangoFechas(fecha: String, tipo: String): Pair<String, String> {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = formato.parse(fecha)!!

        return when (tipo) {
            getString(R.string.opcion_semana) -> {
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                val fechaInicio = formato.format(calendar.time)

                calendar.add(Calendar.DAY_OF_WEEK, 6)
                val fechaFin = formato.format(calendar.time)

                Pair(fechaInicio, fechaFin)
            }
            getString(R.string.opcion_mes) -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val fechaInicio = formato.format(calendar.time)

                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val fechaFin = formato.format(calendar.time)

                Pair(fechaInicio, fechaFin)
            }
            else -> Pair(fecha, fecha)
        }
    }

    private fun procesarResultados(documents: QuerySnapshot) {
        var totalCalorias = 0.0
        var totalProteinas = 0.0
        var totalGrasas = 0.0
        var totalCarbohidratos = 0.0
        var totalSal = 0.0

        for (document in documents) {
            totalCalorias += document.getDouble("valorEnergeticoTotal") ?: 0.0
            totalProteinas += document.getDouble("proteinasTotales") ?: 0.0
            totalGrasas += document.getDouble("grasasTotales") ?: 0.0
            totalCarbohidratos += document.getDouble("carbohidratosTotales") ?: 0.0
            totalSal += document.getDouble("salTotal") ?: 0.0
        }

        tvCalorias.text = getString(R.string.calorias_totales, totalCalorias.toInt())
        tvProteinas.text = getString(R.string.proteinas_totales, totalProteinas)
        tvGrasas.text = getString(R.string.grasas_totales, totalGrasas)
        tvCarbohidratos.text = getString(R.string.carbohidratos_totales, totalCarbohidratos)
        tvSal.text = getString(R.string.sal_total, totalSal)
    }

    private fun mostrarDatosVacios() {
        tvCalorias.text = getString(R.string.calorias_vacias)
        tvProteinas.text = getString(R.string.proteinas_vacias)
        tvGrasas.text = getString(R.string.grasas_vacias)
        tvCarbohidratos.text = getString(R.string.carbohidratos_vacias)
        tvSal.text = getString(R.string.sal_vacia)
        Toast.makeText(this, getString(R.string.no_datos), Toast.LENGTH_SHORT).show()
    }
}
