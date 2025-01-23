package com.example.aplicacionalarcos

import adaptadorNuevasComidas.NuevaComidaAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionalarcos.databinding.ActivityNuevasComidasBinding
import com.google.firebase.firestore.FirebaseFirestore
import modelosNuevasComidas.Ingrediente

class NuevasComidas : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityNuevasComidasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Vincular la vista con ViewBinding
        binding = ActivityNuevasComidasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Ajustar insets para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del botón "Atrás"
        binding.obAtras2.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

            obtenerIngredientes()

    }

    private fun obtenerIngredientes() {
        db.collection("ingredientes")
            .get()
            .addOnSuccessListener { result ->
                val listaIngredientes = mutableListOf<Ingrediente>()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val carbohidratos = document.getString("carbohidratos") ?: "0"
                    val grasas = document.getString("grasas") ?: "0"
                    val proteinas = document.getString("proteinas") ?: "0"
                    val sal = document.getString("sal") ?: "0"
                    val valorEnergetico = document.getString("valorEnergetico") ?: "0"

                    listaIngredientes.add(
                        Ingrediente(nombre, carbohidratos, grasas, proteinas, sal, valorEnergetico)
                    )
                }

                // Configurar el RecyclerView
                binding.RVNuevasComidas.layoutManager = LinearLayoutManager(this)
                binding.RVNuevasComidas.adapter = NuevaComidaAdapter(listaIngredientes)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al obtener los ingredientes", exception)
            }
    }


}
