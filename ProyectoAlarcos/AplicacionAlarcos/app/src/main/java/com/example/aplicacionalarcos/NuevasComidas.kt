package com.example.aplicacionalarcos

import adaptadorNuevasComidas.NuevaComidaAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionalarcos.databinding.ActivityNuevasComidasBinding
import com.google.firebase.firestore.FirebaseFirestore
import modelosNuevasComidas.Ingrediente
import objetos.UserSession

class NuevasComidas : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityNuevasComidasBinding
    private lateinit var adapter: NuevaComidaAdapter
    private val listaIngredientes = mutableListOf<Ingrediente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNuevasComidasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.RVNuevasComidas.layoutManager = LinearLayoutManager(this)

        // Inicializar adapter vacío
        adapter = NuevaComidaAdapter(listaIngredientes) { ingrediente ->
            Toast.makeText(this,
                getString(R.string.seleccionaste_ingrediente, ingrediente.nombre), Toast.LENGTH_SHORT).show()
        }
        binding.RVNuevasComidas.adapter = adapter

        obtenerIngredientes()

        // Botón para volver atrás
        binding.obAtras2.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        // Botón para añadir un nuevo ingrediente
        binding.obAnadirIngredientes.setOnClickListener {
            val intent = Intent(this, ActivityIngredientes::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Botón para eliminar ingredientes seleccionados
        binding.obBorrar.setOnClickListener {
            if (::adapter.isInitialized) {
                adapter.eliminarIngredientesSeleccionados()
            }
        }

        // Botón para añadir una nueva comida con ingredientes seleccionados
        binding.obAnadirComida.setOnClickListener {
            if (::adapter.isInitialized) {
                val seleccionados = adapter.getSeleccionados()
                val nombrePlato = binding.EtNombrePlato.text.toString()

                if (seleccionados.isNotEmpty() && nombrePlato.isNotBlank()) {
                    val intent = Intent(this, ActivityCantidadIngredientes::class.java).apply {
                        putExtra("ingredientesSeleccionados", ArrayList(seleccionados))
                        putExtra("nombrePlato", nombrePlato)
                    }
                    startActivity(intent)
                } else {
                    val mensaje = if (seleccionados.isEmpty()) getString(R.string.no_has_seleccionado_ning_n_ingrediente)
                    else getString(R.string.debes_escribir_un_nombre_para_el_plato)
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("Adapter", "El adaptador no ha sido inicializado aún")
            }
        }
    }

    private fun obtenerIngredientes() {
        val userId = UserSession.id

        db.collection("ingredientes")
            .whereEqualTo("userId", userId) // Filtramos por el usuario actual
            .get()
            .addOnSuccessListener { result ->
                listaIngredientes.clear()

                for (document in result) {
                    val ingrediente = Ingrediente(
                        id = document.id,
                        nombre = document.getString("nombre") ?: "Sin nombre",
                        carbohidratosPor100g = document.getDouble("carbohidratos") ?: 0.0,
                        grasasPor100g = document.getDouble("grasas") ?: 0.0,
                        proteinasPor100g = document.getDouble("proteinas") ?: 0.0,
                        salPor100g = document.getDouble("sal") ?: 0.0,
                        valorEnergeticoPor100g = document.getDouble("valorEnergetico") ?: 0.0
                    )
                    listaIngredientes.add(ingrediente)
                }

                adapter.notifyDataSetChanged() // Refrescar RecyclerView
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener ingredientes", e)
            }
    }
}
