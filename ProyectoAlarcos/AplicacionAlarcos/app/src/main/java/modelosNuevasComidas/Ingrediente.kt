package modelosNuevasComidas

import java.io.Serializable

data class Ingrediente(
    val id: String = "",  // ID único del documento en Firestore
    val nombre: String = "",
    val valorEnergeticoPor100g: Double = 0.0,
    val grasasPor100g: Double = 0.0,
    val carbohidratosPor100g: Double = 0.0,
    val proteinasPor100g: Double = 0.0,
    val salPor100g: Double = 0.0
) : Serializable {

    constructor() : this("", "", 0.0, 0.0, 0.0, 0.0, 0.0)

    override fun toString(): String {
        return """
        - $nombre:
          • Energía: ${valorEnergeticoPor100g} Kcal/100g
          • Grasas: ${grasasPor100g} g/100g
          • Carbohidratos: ${carbohidratosPor100g} g/100g
          • Proteínas: ${proteinasPor100g} g/100g
          • Sal: ${salPor100g} g/100g
        """.trimIndent()
    }
}
