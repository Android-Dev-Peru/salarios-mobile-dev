package pe.dev.android

import pe.dev.android.models.*
import kotlin.math.floor
import kotlin.math.round

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

    fun highlightsPorSeniority(): Map<String, HighlightsPorSeniority>{
        return entries.groupBy { it.seniority }.mapValues { (seniority, entriesBySeniority) ->
            val max = entriesBySeniority.maxOf { round( it.salarioNormalizado()) }
            val min = entriesBySeniority.minOf { round(it.salarioNormalizado()) }

            val promedio = entriesBySeniority.salarioNormalizadoPromedio()
            val porcentajeComparadoAOtrosSeniorities = entries
                .filter { it.seniority != seniority }
                .groupBy { it.seniority }
                .map { otroSeniority ->
                    val otroPromedio = otroSeniority.value.salarioNormalizadoPromedio()
                    val pair = otroSeniority.key to floor(otroPromedio / promedio * 100).toInt() - 100
                    pair
                }
                .toMap()

            HighlightsPorSeniority(
                porcentajeComparadoAOtrosSeniorities = porcentajeComparadoAOtrosSeniorities,
                minNormalizado = min,
                maxNormalizado = max,
            )
        }
    }

    /**
     * Esta funcion asume que:
     * - Cada pais tiene una sola moneda local. Quizas esto no aplique en todos los paises.
     * - USD: moneda extranjera
     */
    private fun cuantoMasGananEnUSD(entries: List<Entry>): Double {
        val avg = entries
            .groupBy { it.moneda }
            .map { it.key to it.value.salarioNormalizadoPromedio() }

        val avgUSD = avg.first { it.first == Moneda.USD }.second
        val avgAllOther = avg.filter { it.first != Moneda.USD }.map { it.second }.average()
        return (avgUSD / avgAllOther) - 1.0
    }

    private fun porcentajePorCargo(entries: List<Entry>): Map<String, Double> {
        val entriesInUSD = entries.filter { it.moneda == Moneda.USD }
        val groupByCargo = entriesInUSD.groupBy { it.seniority }

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
                val promedioMonedaLocal = it.value.salarioNormalizadoPromedio()
                val promedioEnElQueSalarioLocalEsMasBajoQueUSD = (promedioEnDolares / promedioMonedaLocal) - 1.0
                it.key to round(promedioEnElQueSalarioLocalEsMasBajoQueUSD * 100).toInt()
            }
            .toMap()
        return promedioEnElQueSalarioLocalEsMasBajoQueUSD
    }

    private fun List<Entry>.salarioNormalizadoPromedio(): Double {
        return map { it.salarioNormalizado() }.average()
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
