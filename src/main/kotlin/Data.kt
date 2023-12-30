package pe.dev.android

/**
 * Tipo de cambio de la moneda a dólares.
 * Actualizado a: 29 de diciembre de 2023.
 */
enum class Moneda(val conversionRate: Double) {
    PEN(0.27),
    USD(1.0),
    COP(0.00026),
    MXN(0.059),
    CLP(0.0011),
}

data class Entry(
    val timestamp: String,
    val genero: String,
    val pais: String,
    val anosDeExperiencia: String,
    val cargo: String,
    val empresa: String,
    val modalidad: String,
    val tecnologias: List<String>,
    val moneda: Moneda,
    val salario: Double
) {

    fun salarioNormalizado(): Double = salario * moneda.conversionRate
}