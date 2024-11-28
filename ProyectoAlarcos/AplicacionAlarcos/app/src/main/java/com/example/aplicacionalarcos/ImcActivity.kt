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
            calcularIMC()
        }
    }

    //Metodo para calcular IMC
    private fun calcularIMC() {
        val pesoText = binding.EtPeso.text.toString()
        val alturaText = binding.EtAltura.text.toString()

        if (pesoText.isNotEmpty() && alturaText.isNotEmpty()) {
            try {
                val peso = pesoText.toDouble()
                val altura = alturaText.toDouble() / 100 // Convertir centímetros a metros

                if (peso > 0 && altura > 0) {
                    val imc = peso / (altura * altura) // Fórmula del IMC

                    if (imc < 18.5) {
                        binding.IVMediorIMC.setImageResource(R.drawable.minuspesocir) // Peso insuficiente
                        binding.TVIMCRes.text = getString(R.string.MinusPeso) + " " + String.format("%.2f", imc)
                    } else if (imc in 18.5..24.9) {
                        binding.IVMediorIMC.setImageResource(R.drawable.recomendado) // Peso normal
                        binding.TVIMCRes.text = getString(R.string.PesoRecomendado) + " " + String.format("%.2f", imc)
                    } else {
                        binding.IVMediorIMC.setImageResource(R.drawable.sobrepesocir) // Sobrepeso
                        binding.TVIMCRes.text = getString(R.string.SobrePeso) + " " + String.format("%.2f", imc)
                    }

                } else {
                    Toast.makeText(this,
                        getString(R.string.el_peso_y_la_altura_deben_ser_mayores_a_cero), Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(this,
                    getString(R.string.introduce_valores_num_ricos_v_lidos), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this,
                getString(R.string.por_favor_completa_todos_los_campos), Toast.LENGTH_SHORT).show()
        }
    }
}
