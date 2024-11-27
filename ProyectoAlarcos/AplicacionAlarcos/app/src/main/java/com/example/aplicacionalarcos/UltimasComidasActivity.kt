package com.example.aplicacionalarcos

import ComidaAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionalarcos.databinding.ActivityUltimasComidasBinding
import modelosUltimasComidas.Comida
import modelosUltimasComidas.Ingrediente

class UltimasComidasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUltimasComidasBinding
    private lateinit var comidaAdapter: ComidaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Vincular la vista con ViewBinding
        binding = ActivityUltimasComidasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajustar insets para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Lista de comidas
        val comidas = mutableListOf(
            Comida(
                "Pizza",
                listOf(
                    Ingrediente("Harina", "200g"),
                    Ingrediente("Queso", "100g"),
                    Ingrediente("Tomate", "50g")
                )
            ),
            Comida(
                "Ensalada",
                listOf(
                    Ingrediente("Lechuga", "1 unidad"),
                    Ingrediente("Tomate", "2 unidades"),
                    Ingrediente("Aceite", "50ml")
                )
            ),
            Comida(
                "Hamburguesa",
                listOf(
                    Ingrediente("Carne", "150g"),
                    Ingrediente("Pan", "2 piezas"),
                    Ingrediente("Lechuga", "1 hoja")
                )
            ),
            Comida(
                "Sopa de Pollo",
                listOf(
                    Ingrediente("Pollo", "200g"),
                    Ingrediente("Zanahoria", "1 unidad"),
                    Ingrediente("Fideos", "50g")
                )
            ),
            Comida(
                "Tacos",
                listOf(
                    Ingrediente("Tortillas", "3 unidades"),
                    Ingrediente("Carne", "200g"),
                    Ingrediente("Cebolla", "50g")
                )
            ),
            Comida(
                "Lasagna",
                listOf(
                    Ingrediente("Pasta", "300g"),
                    Ingrediente("Carne molida", "200g"),
                    Ingrediente("Queso", "100g")
                )
            ),
            Comida(
                "Paella",
                listOf(
                    Ingrediente("Arroz", "250g"),
                    Ingrediente("Mariscos", "300g"),
                    Ingrediente("Azafrán", "1 pizca")
                )
            ),
            Comida(
                "Sandwich",
                listOf(
                    Ingrediente("Pan", "2 rebanadas"),
                    Ingrediente("Jamón", "50g"),
                    Ingrediente("Queso", "50g")
                )
            ),
            Comida(
                "Chili con Carne",
                listOf(
                    Ingrediente("Carne molida", "300g"),
                    Ingrediente("Frijoles", "200g"),
                    Ingrediente("Tomate", "100g")
                )
            ),
            Comida(
                "Ratatouille",
                listOf(
                    Ingrediente("Berenjena", "1 unidad"),
                    Ingrediente("Calabacín", "1 unidad"),
                    Ingrediente("Tomate", "100g")
                )
            )
        )


        // Configurar RecyclerView
        comidaAdapter = ComidaAdapter(comidas)
        binding.RVComidas.adapter = comidaAdapter
        binding.RVComidas.layoutManager = LinearLayoutManager(this)

        // Configurar botón eliminar
        binding.EliminarBt.setOnClickListener {
            comidaAdapter.eliminarSeleccionados()
            Toast.makeText(this, "Elementos eliminados", Toast.LENGTH_SHORT).show()
        }

        // Configuración del botón "Atrás"
        binding.obAtras2.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }
}
