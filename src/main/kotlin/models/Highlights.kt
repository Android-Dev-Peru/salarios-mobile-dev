package pe.dev.android.models

data class Highlights(
    val minNormalizado: Double,
    val maxNormalizado: Double,
    val cuantoMasPorcentajeGanasEnUSD: Int,
    val porcentajePorCargo: Map<String, Double>,
)

data class HighlightsPorPais(
    val minMonedaLocal: Double?,
    val maxMonedaLocal: Double?,
    val medianMonedaLocal: Double?,
    val minUSD: Double?,
    val maxUSD: Double?,
    val medianUSD: Double?,
    val topEmpresasMonedaLocal: List<String>,
    val topEmpresasUSD: List<String>,
)

data class HighlightsPorMoneda(
    /**
     * Ejemplo:
     * Moneda.PEN -> 50%, significa que en promedio, los que ganan en PEN ganan 50% menos que los que ganan en USD
     * Moneda.COP -> 100%, significa que en promedio, los que ganan en COP ganan 100% menos que los que ganan en USD
     */
    val promedioEnElQueSalarioLocalEsMasBajoQueUSD: Map<Moneda, Int>,
    val minUSD: Double,
    val maxUSD: Double,
    val cuantoMasPorcentajeGanasEnUSD: Int,
    val porcentajeGananEnDolaresTrabajanRemote: Int,
)