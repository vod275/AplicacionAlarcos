package modelosNuevasComidas

import java.io.Serializable

data class Ingrediente(
    val nombre: String,
    val valorEnergeticoPor100g: Double,
    val proteinasPor100g: Double, // Proteínas por cada 100g o 100ml
    val grasasPor100g: Double, // Grasas por cada 100g o 100ml
    val salPor100g: Double, // Sal por cada 100g o 100ml
    val carbohidratosPor100g: Double // Carbohidratos por cada 100g o 100ml
) : Serializable
