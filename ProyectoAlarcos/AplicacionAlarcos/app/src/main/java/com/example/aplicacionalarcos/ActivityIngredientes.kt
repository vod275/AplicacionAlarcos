package com.example.aplicacionalarcos

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityIngredientesBinding
import com.google.firebase.firestore.FirebaseFirestore

class ActivityIngredientes : AppCompatActivity() {

    private lateinit var binding: ActivityIngredientesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración de View Binding
        binding = ActivityIngredientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del botón Guardar
        binding.btnGuardar.setOnClickListener {
            val nombreIngrediente = binding.tvNombreAjustes.editText?.text.toString()
            val valorEnergetico = binding.etValorEnergetico.text.toString()
            val grasas = binding.etGrasas.text.toString()
            val carbohidratos = binding.etCarbohidratos.text.toString()
            val proteinas = binding.etProteinas.text.toString()
            val sal = binding.etSal.text.toString()

            // Validación de los campos
            if (nombreIngrediente.isEmpty() || valorEnergetico.isEmpty() || grasas.isEmpty() ||
                carbohidratos.isEmpty() || proteinas.isEmpty() || sal.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear datos para Firestore
            val ingredienteData = mapOf(
                "nombre" to nombreIngrediente,
                "valorEnergetico" to valorEnergetico,
                "grasas" to grasas,
                "carbohidratos" to carbohidratos,
                "proteinas" to proteinas,
                "sal" to sal
            )

            // Guardar en Firestore
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("ingredientes")
                .document(nombreIngrediente.replace(" ", "_"))
                .set(ingredienteData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Ingrediente guardado correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Configuración del botón Volver
        binding.btnVolver.setOnClickListener {
            finish() // Cierra esta actividad y regresa a la anterior
        }
    }
}
