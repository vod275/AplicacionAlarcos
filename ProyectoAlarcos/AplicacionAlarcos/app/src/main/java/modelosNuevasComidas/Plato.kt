package modelosNuevasComidas

data class Plato(
    var nombre: String = "",
    var ingredientes: List<Ingrediente> = emptyList(),
    var cantidad: List<Int> = emptyList(),
    var caloriasTotales: Double = 0.0,
    var proteinasTotales: Double = 0.0,
    var grasasTotales: Double = 0.0,
    var carbohidratosTotales: Double = 0.0,
    var fechaRegistro: String = "" // Aquí guardamos la fecha del plato
) {
    // Este constructor sin argumentos es utilizado por Firestore
    constructor() : this("", emptyList(), emptyList())
}