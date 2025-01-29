package com.example.aplicacionalarcos

import adaptadorNuevasComidas.NuevaComidaAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
    private lateinit var adapter: NuevaComidaAdapter // Adaptador para el RecyclerView
    private val listaIngredientes = mutableListOf<Ingrediente>() // Lista mutable para actualizar dinámicamente

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

        // Configurar el RecyclerView
        binding.RVNuevasComidas.layoutManager = LinearLayoutManager(this)

        // Obtener y mostrar los ingredientes
        obtenerIngredientes()

        // Configurar el botón para eliminar ingredientes seleccionados
        binding.obBorrar.setOnClickListener {
            eliminarIngredientesSeleccionados()
        }

        // Configurar el botón para guardar los seleccionados y enviarlos a otra actividad
        binding.obAnadirComida.setOnClickListener {
            if (::adapter.isInitialized) {
                val seleccionados = adapter.getSeleccionados() // Obtener la lista seleccionada
                val nombrePlato = binding.EtNombrePlato.text.toString() // Obtener el nombre del plato del EditText

                if (seleccionados.isNotEmpty() && nombrePlato.isNotBlank()) {
                    val intent = Intent(this, ActivityCantidadIngredientes::class.java).apply {
                        putExtra("ingredientesSeleccionados", ArrayList(seleccionados)) // Convertir a ArrayList
                        putExtra("nombrePlato", nombrePlato) // Añadir el nombre del plato como extra
                    }
                    startActivity(intent)
                } else {
                    // Mostrar mensaje según lo que falte
                    val mensaje = if (seleccionados.isEmpty()) {
                        "No has seleccionado ningún ingrediente."
                    } else {
                        "Debes escribir un nombre para el plato."
                    }
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Espere a que se carguen los ingredientes.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para obtener ingredientes desde Firestore
    private fun obtenerIngredientes() {
        db.collection("ingredientes")
            .get()
            .addOnSuccessListener { result ->
                listaIngredientes.clear() // Limpiar la lista antes de agregar nuevos datos
                for (document in result) {
                    val id = document.id // Obtener el ID único del documento
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val carbohidratos = document.getString("carbohidratos")?.toDoubleOrNull() ?: 0.0
                    val grasas = document.getString("grasas")?.toDoubleOrNull() ?: 0.0
                    val proteinas = document.getString("proteinas")?.toDoubleOrNull() ?: 0.0
                    val sal = document.getString("sal")?.toDoubleOrNull() ?: 0.0
                    val valorEnergetico = document.getString("valorEnergetico")?.toDoubleOrNull() ?: 0.0

                    listaIngredientes.add(
                        Ingrediente(
                            id = id, // Guardar el ID único
                            nombre = nombre,
                            valorEnergeticoPor100g = valorEnergetico,
                            proteinasPor100g = proteinas,
                            grasasPor100g = grasas,
                            salPor100g = sal,
                            carbohidratosPor100g = carbohidratos
                        )
                    )
                }

                // Inicializar y asignar el adaptador al RecyclerView
                adapter = NuevaComidaAdapter(binding, listaIngredientes) { ingrediente ->
                    Toast.makeText(this, "Seleccionaste: ${ingrediente.nombre}", Toast.LENGTH_SHORT).show()
                }
                binding.RVNuevasComidas.adapter = adapter

                Log.d("Firestore", "Ingredientes cargados: ${listaIngredientes.size}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener ingredientes", e)
            }
    }

    // Función para eliminar los ingredientes seleccionados de Firestore
    private fun eliminarIngredientesSeleccionados() {
        if (!::adapter.isInitialized) {
            Toast.makeText(this, "Espere a que se carguen los ingredientes.", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredientesAEliminar = adapter.getSeleccionados()

        if (ingredientesAEliminar.isEmpty()) {
            Toast.makeText(this, "No has seleccionado ningún ingrediente.", Toast.LENGTH_SHORT).show()
            return
        }

        for (ingrediente in ingredientesAEliminar) {
            db.collection("ingredientes")
                .document(ingrediente.id) // Usamos el ID único para eliminar
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "Ingrediente ${ingrediente.nombre} eliminado correctamente")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al eliminar ${ingrediente.nombre}: ${e.message}")
                }
        }

        // Eliminar de la lista local y actualizar la UI
        listaIngredientes.removeAll(ingredientesAEliminar)
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Ingredientes eliminados.", Toast.LENGTH_SHORT).show()
    }
}
