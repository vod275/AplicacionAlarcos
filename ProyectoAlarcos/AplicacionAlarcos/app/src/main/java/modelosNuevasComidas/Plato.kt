package modelosNuevasComidas

data class Plato(
    var nombre: String,
    var ingredientes: List<Ingrediente>,
    var cantidad: List<Int>,
    var caloriasTotales: Double = 0.0,
    var proteinasTotales: Double = 0.0,
    var grasasTotales: Double = 0.0,
    var carbohidratosTotales: Double = 0.0
)
