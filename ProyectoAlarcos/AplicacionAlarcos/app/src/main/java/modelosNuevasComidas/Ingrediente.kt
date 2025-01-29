package modelosNuevasComidas

import java.io.Serializable

data class Ingrediente(
    val nombre: String = "",
    val valorEnergeticoPor100g: Double = 0.0,
    val proteinasPor100g: Double = 0.0,
    val grasasPor100g: Double = 0.0,
    val salPor100g: Double = 0.0,
    val carbohidratosPor100g: Double = 0.0
) : Serializable {
    // Constructor vacío necesario para deserialización de Firestore
    constructor() : this("", 0.0, 0.0, 0.0, 0.0, 0.0)
}
