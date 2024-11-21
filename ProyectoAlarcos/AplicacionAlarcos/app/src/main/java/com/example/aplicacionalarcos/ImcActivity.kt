package com.example.aplicacionalarcos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionalarcos.databinding.ActivityImcBinding

class ImcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImcBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.TVIMCRes.text = ""

        // Configuración del botón "Atrás"
        binding.obAtras.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        // Configuración del botón para calcular IMC
        binding.btnAcceptDialog.setOnClickListener {
            val pesoText = binding.EtPeso.text.toString()
            val alturaText = binding.EtAltura.text.toString()

            if (pesoText.isNotEmpty() && alturaText.isNotEmpty()) {
                try {
                    val peso = pesoText.toDouble()
                    val altura = alturaText.toDouble() / 100 // Convertir centímetros a metros

                    if (peso > 0 && altura > 0) {
                        val imc = peso / (altura * altura)
                        val imcFormateado = String.format("%.2f", imc)// Fórmula del IMC
                        if (imc < 18.5) {
                            binding.IVMediorIMC.setImageResource(R.drawable.minuspesocir) // Peso insuficiente
                            binding.TVIMCRes.text = "PESO POR DEBAJO DEL RECOMENDADO TU IMC ES: $imcFormateado"
                        } else if (imc in 18.5..24.9) {
                            binding.IVMediorIMC.setImageResource(R.drawable.recomendado) // Peso normal
                            binding.TVIMCRes.text = "PESO EN EL RANGO RECOMENDADO TU IMC ES: $imcFormateado"

                        } else {
                            binding.IVMediorIMC.setImageResource(R.drawable.sobrepesocir) // Sobrepeso
                            binding.TVIMCRes.text = "PESO POR ENCIMA DEL RECOMENDADO TU IMC ES: $imcFormateado"
                        }
                    } else {
                        Toast.makeText(this, "El peso y la altura deben ser mayores a cero.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Introduce valores numéricos válidos.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
