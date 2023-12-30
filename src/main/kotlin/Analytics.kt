package pe.dev.android

import kotlin.math.floor
import kotlin.math.round

data class Highlights(
    val minNormalizado: Double,
    val maxNormalizado: Double,
    val cuantoMasPorcentajeGanasEnUSD: Int,
    val porcentajePorCargo: Map<String, Double>,
)

class Analytics(private val entries: List<Entry>) {

    fun highlights(): Highlights {
        val max = entries.maxOf { it.salarioNormalizado() }
        val min = entries.minOf { it.salarioNormalizado() }

        return Highlights(
            minNormalizado = min,
            maxNormalizado = max,
            cuantoMasPorcentajeGanasEnUSD = floor(cuantoMasGananEnUSD(entries) * 100).toInt(),
            porcentajePorCargo = porcentajePorCargo(entries),
        )
    }

    /**
     * Esta funcion asume que:
     * - Cada pais tiene una sola moneda local. Quizas esto no aplique en todos los paises.
     * - USD: moneda extranjera
     */
    private fun cuantoMasGananEnUSD(entries: List<Entry>): Double {
        val avg = entries
            .groupBy { it.moneda }
            .map { it.key to it.value.map { it.salarioNormalizado() }.average() }

        val avgUSD = avg.first { it.first == Moneda.USD }.second
        val avgAllOther = avg.filter { it.first != Moneda.USD }.map { it.second }.average()
        return (avgUSD / avgAllOther) - 1.0
    }

    private fun porcentajePorCargo(entries: List<Entry>): Map<String, Double> {
        val entriesInUSD = entries.filter { it.moneda == Moneda.USD }
        val groupByCargo = entriesInUSD.groupBy { it.cargo }

        return groupByCargo.mapValues { (cargo, entries) ->
            round(entries.size.toDouble() / entriesInUSD.size * 100)
        }
    }

}

interface AnalyticsDisplay {
    fun display(highlights: Highlights)
}

class AnalyticsConsole : AnalyticsDisplay {
    override fun display(highlights: Highlights) {
        println("---------------------------------------------------")
        println("⭐️ HIGHLIGHTS ⭐")
        println("---------------------------------------------------")
        println("Salario más bajo: ${highlights.minNormalizado} ${Moneda.USD}")
        println("Salario más alto: ${highlights.maxNormalizado} ${Moneda.USD}")
        println("Los que ganan en USD, tienen salarios ${highlights.cuantoMasPorcentajeGanasEnUSD}% más altos que sus compatriotas que ganan en moneda local")
        println("La distribucion de salarios en USD por cargo es: ${highlights.porcentajePorCargo.map { "${it.key}: ${it.value}%" }.joinToString()}")
    }

}