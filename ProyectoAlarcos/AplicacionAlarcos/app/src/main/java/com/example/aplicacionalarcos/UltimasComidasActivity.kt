package com.example.aplicacionalarcos

import adaptadorUltimasComidas.ComidaAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionalarcos.databinding.ActivityUltimasComidasBinding
import com.google.firebase.firestore.FirebaseFirestore
import modelosNuevasComidas.Plato
import objetos.UserSession

class UltimasComidasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUltimasComidasBinding
    private lateinit var comidaAdapter: ComidaAdapter
    private val db = FirebaseFirestore.getInstance()
    private val listaComidas = mutableListOf<Pair<String, Plato>>() // Guardamos (nombreDocumento, Plato)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUltimasComidasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar RecyclerView
        comidaAdapter = ComidaAdapter(listaComidas)
        binding.RVComidas.adapter = comidaAdapter
        binding.RVComidas.layoutManager = LinearLayoutManager(this)

        // Cargar datos de Firebase filtrando por UserSession.id
        cargarComidasDesdeFirebase()

        // Botón eliminar
        binding.EliminarBt.setOnClickListener {
            comidaAdapter.eliminarSeleccionados()
            Toast.makeText(this, "Elementos eliminados", Toast.LENGTH_SHORT).show()
        }

        // Botón atrás
        binding.obAtras2.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        // Botón añadir comida
        binding.obAnadirComida.setOnClickListener {
            val intent = Intent(this, NuevasComidas::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    private fun cargarComidasDesdeFirebase() {
        val userId = UserSession.id
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("platos")
            .whereEqualTo("id", userId) // Filtrar solo platos con el mismo ID del usuario
            .get()
            .addOnSuccessListener { documents ->
                listaComidas.clear()
                for (document in documents) {
                    val comida = document.toObject(Plato::class.java)
                    listaComidas.add(Pair(document.id, comida)) // Guardamos el nombre del documento
                }
                comidaAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar las comidas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
