package com.example.aplicacionalarcos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityIngredientesBinding
import com.google.firebase.firestore.FirebaseFirestore
import objetos.UserSession

class ActivityIngredientes : AppCompatActivity() {

    private lateinit var binding: ActivityIngredientesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración de View Binding
        binding = ActivityIngredientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del botón Guardar
        binding.btnGuardar.setOnClickListener {
            val userId = UserSession.id // ID del usuario actual
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

            // Crear datos para Firestore (ahora incluyendo el userId)
            val ingredienteData = mapOf(
                "userId" to userId, // Guardamos el ID del usuario en Firestore
                "nombre" to nombreIngrediente,
                "valorEnergetico" to valorEnergetico.toDouble(),
                "grasas" to grasas.toDouble(),
                "carbohidratos" to carbohidratos.toDouble(),
                "proteinas" to proteinas.toDouble(),
                "sal" to sal.toDouble()
            )

            // Guardar en Firestore con un ID automático
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("ingredientes")
                .add(ingredienteData)
                .addOnSuccessListener {
                    Toast.makeText(this,
                        getString(R.string.ingrediente_guardado_correctamente), Toast.LENGTH_SHORT).show()
                    limpiarCampos() // Limpia los campos después de guardar
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,
                        getString(R.string.error_al_guardar, e.message), Toast.LENGTH_SHORT).show()
                }
        }

        // Configuración del botón Volver
        binding.btnVolver.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    // Función para limpiar los campos después de guardar
    private fun limpiarCampos() {
        binding.etSal.text?.clear()
        binding.etGrasas.text?.clear()
        binding.etCarbohidratos.text?.clear()
        binding.etProteinas.text?.clear()
        binding.etValorEnergetico.text?.clear()
        binding.tvNombreAjustes.editText?.text?.clear()
    }
}
