package pe.dev.android

import kotlin.math.floor
import kotlin.math.round

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

    fun highlightsPorPais(): Map<String, HighlightsPorPais> {
        val entriesByPais = entries.groupBy { it.pais }

        return entriesByPais.mapValues { (_, entries) ->
            val entriesInMonedaLocal = entries.filter { it.moneda != Moneda.USD }
            val entriesInUSD = entries.filter { it.moneda == Moneda.USD }

            HighlightsPorPais(
                minMonedaLocal = entriesInMonedaLocal.minOfOrNull { it.salario },
                maxMonedaLocal = entriesInMonedaLocal.maxOfOrNull { it.salario },
                medianMonedaLocal = entriesInMonedaLocal.map { it.salario }.median(),
                minUSD = entriesInUSD.minOfOrNull { it.salario },
                maxUSD = entriesInUSD.maxOfOrNull { it.salario },
                medianUSD = entriesInUSD.map { it.salario }.median(),
                topEmpresasMonedaLocal = empresasConSalarioMasAlto(entriesInMonedaLocal),
                topEmpresasUSD = empresasConSalarioMasAlto(entriesInUSD),
            )
        }
    }

    fun highlightsPorMoneda(): HighlightsPorMoneda {
        val entriesInUSD = entries.filter { it.moneda == Moneda.USD }

        return HighlightsPorMoneda(
            minUSD = entriesInUSD.minOf { it.salarioNormalizado() },
            maxUSD = entriesInUSD.maxOf { it.salarioNormalizado() },
            cuantoMasPorcentajeGanasEnUSD = floor(cuantoMasGananEnUSD(entries) * 100).toInt(),
            promedioEnElQueSalarioLocalEsMasBajoQueUSD = promedioEnElQueSalarioLocalEsMasBajoQueUSD(entries),
            porcentajeGananEnDolaresTrabajanRemote = floor(entriesInUSD
                .filter { it.modalidad == "Remoto" }
                .size.toDouble() / entriesInUSD.size * 100
            ).toInt(),
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

    private fun empresasConSalarioMasAlto(entries: List<Entry>): List<String> {
        return entries
            .asSequence()
            .filter { it.empresa.isNotBlank() }
            .groupBy { it.empresa }
            .map { it.key to it.value.map { it.salario }.max() }
            .sortedByDescending { it.second }
            .take(3)
            .map { it.first }
            .toList()
    }

    private fun promedioEnElQueSalarioLocalEsMasBajoQueUSD(entries: List<Entry>): Map<Moneda, Int> {
        val entriesInUSD = entries.filter { it.moneda == Moneda.USD }
        val entriesInMonedaLocal = entries.filter { it.moneda != Moneda.USD }

        val promedioEnDolares = entriesInUSD.map { it.salario }.average()
        val promedioEnElQueSalarioLocalEsMasBajoQueUSD = entriesInMonedaLocal
            .groupBy { it.moneda }
            .map {
                val promedioMonedaLocal = it.value.map { it.salarioNormalizado() }.average()
                val promedioEnElQueSalarioLocalEsMasBajoQueUSD = (promedioEnDolares / promedioMonedaLocal) - 1.0
                it.key to floor(promedioEnElQueSalarioLocalEsMasBajoQueUSD * 100).toInt()
            }
            .toMap()
        return promedioEnElQueSalarioLocalEsMasBajoQueUSD
    }

    private fun List<Double>.median(): Double? {
        if (isEmpty()) return null
        val sortedList = sorted()
        val mid = size / 2

        return if (size % 2 == 1) {
            sortedList[mid]
        } else {
            (sortedList[mid - 1] + sortedList[mid]) / 2.0
        }
    }
}

interface AnalyticsDisplay {
    fun display(highlights: Highlights)
    fun display(highlights: Map<String,HighlightsPorPais>)
    fun display(highlights: HighlightsPorMoneda)
}

class AnalyticsConsole : AnalyticsDisplay {
    override fun display(highlights: Highlights) {
        header("HIGHLIGHTS", "⭐️")

        println("Salario más bajo: ${highlights.minNormalizado} ${Moneda.USD}")
        println("Salario más alto: ${highlights.maxNormalizado} ${Moneda.USD}")
        println("Los que ganan en USD, tienen salarios ${highlights.cuantoMasPorcentajeGanasEnUSD}% más altos que sus compatriotas que ganan en moneda local")
        println("La distribucion de salarios en USD por cargo es: ${highlights.porcentajePorCargo.map { "${it.key}: ${it.value}%" }.joinToString()}")
    }

    override fun display(highlights: Map<String, HighlightsPorPais>) {
        header("HIGHLIGHTS POR PAIS", "🌎")
        highlights.forEach { (pais, highlights) ->
            println(pais)
            if (highlights.minMonedaLocal == null || highlights.maxMonedaLocal == null || highlights.medianMonedaLocal == null) {
                println("No hay datos en moneda local")
            } else {
                println("Salario más bajo moneda local: ${highlights.minMonedaLocal}")
                println("Salario más alto moneda local: ${highlights.maxMonedaLocal}")
                println("Salario mediano moneda local: ${highlights.medianMonedaLocal}")
            }
            if(highlights.minUSD == null || highlights.maxUSD == null || highlights.medianUSD == null) {
                println("No hay datos en USD")
            } else {
                println("Salario más bajo USD: ${highlights.minUSD} ${Moneda.USD}")
                println("Salario más alto USD: ${highlights.maxUSD} ${Moneda.USD}")
                println("Salario mediano USD: ${highlights.medianUSD} ${Moneda.USD}")
            }
            if(highlights.topEmpresasMonedaLocal.isNotEmpty()) {
                println("Top 3 empresas moneda local: ${highlights.topEmpresasMonedaLocal.joinToString()}")
            }
            if(highlights.topEmpresasUSD.isNotEmpty()) {
                println("Top 3 empresas USD: ${highlights.topEmpresasUSD.joinToString()}")
            }
            linea()
        }
    }

    override fun display(highlights: HighlightsPorMoneda) {
        header("HIGHLIGHTS POR MONEDA", "💰")
        println("Salario más bajo USD: ${highlights.minUSD} ${Moneda.USD}")
        println("Salario más alto USD: ${highlights.maxUSD} ${Moneda.USD}")
        println("Los que ganan en USD, tienen salarios ${highlights.cuantoMasPorcentajeGanasEnUSD}% más altos que sus compatriotas que ganan en moneda local")
        println("Los que ganan en USD, trabajan en modalidad remota ${highlights.porcentajeGananEnDolaresTrabajanRemote}% de las veces")
        println("La distribucion de salarios en USD por pais es: ${highlights.promedioEnElQueSalarioLocalEsMasBajoQueUSD.map { "${it.key}: ${it.value}%" }.joinToString()}")
    }

    private fun linea() {
        println("---------------------------------------------------")
    }

    private fun header(title: String, icon: String) {
        linea()
        println("${icon.repeat(5)} $title ${icon.repeat(5)}")
        linea()
    }
}