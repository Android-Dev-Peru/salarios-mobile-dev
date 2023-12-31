package pe.dev.android

import pe.dev.android.models.*
import kotlin.math.absoluteValue

interface AnalyticsDisplay {
    fun display(highlights: Highlights)
    fun display(highlights: Map<String, HighlightsPorPais>)
    fun display(highlights: HighlightsPorMoneda)
    fun displayBySeniority(highlights: Map<String, HighlightsPorSeniority>)
}

class AnalyticsConsole : AnalyticsDisplay {
    override fun display(highlights: Highlights) {
        header("HIGHLIGHTS", "⭐️")

        println("Salario más bajo: ${highlights.minNormalizado} ${Moneda.USD}")
        println("Salario más alto: ${highlights.maxNormalizado} ${Moneda.USD}")
        println("Los que ganan en USD, tienen salarios ${highlights.cuantoMasPorcentajeGanasEnUSD}% más altos que sus compatriotas que ganan en moneda local")
        println("La distribucion de salarios en USD por cargo es: ${highlights.porcentajePorCargo.map { "${it.key}: ${it.value}%" }.joinToString()}")
        println("La distribución por modalidad es: ${highlights.porcentajePorModalidad.map { "${it.key}: ${it.value}%" }.joinToString()}")
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

    override fun displayBySeniority(highlights: Map<String, HighlightsPorSeniority>) {
        header("HIGHLIGHTS POR SENIORITY", "🥸")
        highlights.forEach { (seniority, highlights) ->
            println(seniority)
            println("Salario más bajo: ${highlights.minNormalizado} ${Moneda.USD}")
            println("Salario más alto: ${highlights.maxNormalizado} ${Moneda.USD}")
            println("Comparado a otros seniorities: \n" +
                    highlights.porcentajeComparadoAOtrosSeniorities
                        .map {
                            val comparacion = when {
                                it.value > 0 -> "gana ${it.value}% más"
                                it.value < 0 -> "gana ${it.value.absoluteValue}% menos"
                                else -> "gana lo mismo"
                            }
                            "  - Un ${it.key} $comparacion"
                        }
                        .joinToString("\n")
            )
            linea()
        }
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