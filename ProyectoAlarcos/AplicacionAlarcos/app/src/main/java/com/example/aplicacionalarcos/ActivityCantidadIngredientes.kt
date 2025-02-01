package com.example.aplicacionalarcos

import adaptadorNuevasComidas.CantidadesIngredientesAdapter
import android.content.Intent
import android.os.Bundle
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplicacionalarcos.databinding.ActivityCantidadIngredientesBinding
import com.google.firebase.firestore.FirebaseFirestore
import modelosNuevasComidas.Ingrediente
import modelosNuevasComidas.Plato
import objetos.UserSession

class ActivityCantidadIngredientes : AppCompatActivity() {
    private lateinit var binding: ActivityCantidadIngredientesBinding
    private val cantidades = arrayListOf<String>() // Lista para guardar las cantidades ingresadas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCantidadIngredientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar los ingredientes seleccionados del Intent
        val ingredientesSeleccionados =
            intent.getSerializableExtra("ingredientesSeleccionados") as? ArrayList<Ingrediente>

        val nombrePlato = intent.getStringExtra("nombrePlato") ?: "Sin nombre"
        Log.d("NombrePlato", "Nombre del plato: $nombrePlato")

        if (ingredientesSeleccionados != null) {
            // Inicializar el ArrayList con valores vacíos para cada ingrediente
            for (i in ingredientesSeleccionados.indices) {
                cantidades.add("") // Inicializamos con cadenas vacías
            }

            // Configurar el RecyclerView
            binding.RVCantidades.layoutManager = LinearLayoutManager(this)
            binding.RVCantidades.adapter = CantidadesIngredientesAdapter(
                ingredientesSeleccionados
            ) { position, cantidad ->
                // Actualizar la cantidad en la posición correspondiente
                cantidades[position] = cantidad
            }
        }

        // Botón para confirmar las cantidades
        binding.btnAccept.setOnClickListener {
            confirmarCantidades(ingredientesSeleccionados, nombrePlato)
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

    private fun confirmarCantidades(ingredientes: ArrayList<Ingrediente>?, nombrePlato: String) {
        if (ingredientes == null) {
            Toast.makeText(this,
                getString(R.string.error_no_se_encontraron_ingredientes), Toast.LENGTH_SHORT).show()
            return
        }

        val cantidadesEnOrden = arrayListOf<Int>() // Lista para almacenar las cantidades en orden

        // Variables para almacenar los totales
        var proteinasTotales = 0.0
        var grasasTotales = 0.0
        var carbohidratosTotales = 0.0
        var salTotal = 0.0
        var valorEnergeticoTotal = 0.0

        // Recorremos cada ingrediente y aplicamos la regla de tres
        for (i in ingredientes.indices) {
            val cantidadIngresada = cantidades[i].toIntOrNull() ?: 0 // Convertir a Int o usar 0 si es inválido

            if (cantidadIngresada <= 0) {
                Toast.makeText(this,
                    getString(R.string.por_favor_ingresa_cantidades_v_lidas), Toast.LENGTH_SHORT).show()
                return
            }

            cantidadesEnOrden.add(cantidadIngresada) // Añadir las cantidades en el mismo orden de los ingredientes

            // Aplicar la regla de tres para cada valor nutricional
            proteinasTotales += (cantidadIngresada * ingredientes[i].proteinasPor100g) / 100
            grasasTotales += (cantidadIngresada * ingredientes[i].grasasPor100g) / 100
            carbohidratosTotales += (cantidadIngresada * ingredientes[i].carbohidratosPor100g) / 100
            salTotal += (cantidadIngresada * ingredientes[i].salPor100g) / 100
            valorEnergeticoTotal += (cantidadIngresada * ingredientes[i].valorEnergeticoPor100g) / 100
        }

        // Obtener el ID de usuario de la sesión
        val userId = UserSession.id
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this,
                getString(R.string.error_no_se_pudo_obtener_el_id_del_usuario), Toast.LENGTH_SHORT).show()
            return
        }

        // Crear el objeto Plato con todos los valores calculados
        val plato = Plato(
            id = userId,  // Asignar el ID del usuario al plato
            nombre = nombrePlato,
            ingredientes = ingredientes,
            cantidad = cantidadesEnOrden,
            proteinasTotales = proteinasTotales,
            grasasTotales = grasasTotales,
            carbohidratosTotales = carbohidratosTotales
        )

        // Log para verificar los datos del plato creado
        Log.d("PlatoCreado", "Plato: ${plato.nombre}")
        Log.d("PlatoCreado", "Ingredientes: ${plato.ingredientes.map { it.nombre }}")
        Log.d("PlatoCreado", "Cantidades: ${plato.cantidad}")
        Log.d("PlatoCreado", "Proteínas Totales: $proteinasTotales")
        Log.d("PlatoCreado", "Grasas Totales: $grasasTotales")
        Log.d("PlatoCreado", "Carbohidratos Totales: $carbohidratosTotales")
        Log.d("PlatoCreado", "Sal Total: $salTotal")
        Log.d("PlatoCreado", "Valor Energético Total: $valorEnergeticoTotal")

        // Guardar el plato en Firestore
        guardarPlatoEnFirestore(plato, valorEnergeticoTotal, salTotal)
    }

    private fun guardarPlatoEnFirestore(plato: Plato, valorEnergeticoTotal: Double, salTotal: Double) {
        val db = FirebaseFirestore.getInstance()

        // Obtener fecha actual
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Convertir el objeto Plato a un mapa para Firestore
        val platoMap = hashMapOf(
            "id" to plato.id,  // Usamos el ID del usuario como ID del plato
            "nombre" to plato.nombre,
            "fechaRegistro" to fechaActual,  // Agregar la fecha
            "ingredientes" to plato.ingredientes.map { ingrediente ->
                mapOf(
                    "nombre" to ingrediente.nombre,
                    "valorEnergeticoPor100g" to ingrediente.valorEnergeticoPor100g,
                    "proteinasPor100g" to ingrediente.proteinasPor100g,
                    "grasasPor100g" to ingrediente.grasasPor100g,
                    "salPor100g" to ingrediente.salPor100g,
                    "carbohidratosPor100g" to ingrediente.carbohidratosPor100g
                )
            },
            "cantidad" to plato.cantidad,
            "proteinasTotales" to plato.proteinasTotales,
            "grasasTotales" to plato.grasasTotales,
            "carbohidratosTotales" to plato.carbohidratosTotales,
            "valorEnergeticoTotal" to valorEnergeticoTotal,
            "salTotal" to salTotal
        )

        // Guardar el documento en Firestore usando el ID de usuario como clave
        db.collection("platos").document(plato.id)
            .set(platoMap)
            .addOnSuccessListener {
                Log.d("Firestore", "Plato guardado con ID: ${plato.id}")
                Toast.makeText(this,
                    getString(R.string.plato_guardado_correctamente), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al guardar el plato", e)
                Toast.makeText(this,
                    getString(R.string.error_al_guardar_el_plato), Toast.LENGTH_SHORT).show()
            }
    }
}
