import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import pe.dev.android.Analytics
import pe.dev.android.models.Moneda

class AnalyticsTest {

    val analytics = Analytics(testEntries)

    @Test
    fun `calcula minimo salario normalizado`() {
        val highlights = analytics.highlights()
        assertEquals(378.0, highlights.minNormalizado)
    }

    @Test
    fun `calcula maximo salario normalizado`() {
        val highlights = analytics.highlights()
        assertEquals(15_000.0, highlights.maxNormalizado)
    }

    @Test
    fun `calcula cuanto mas ganan en USD`() {
        val highlights = analytics.highlights()
        assertEquals(65, highlights.cuantoMasPorcentajeGanasEnUSD)
    }

    @Test
    fun `calcula porcentaje por cargo`() {
        val highlights = analytics.highlights()
        assertEquals(20.0, highlights.porcentajePorCargo["Semi/Mid"])
        assertEquals(71.0, highlights.porcentajePorCargo["Senior"])
        assertEquals(3.0, highlights.porcentajePorCargo["Contractor"])
        assertEquals(3.0, highlights.porcentajePorCargo["Tech Lead"])
        assertEquals(3.0, highlights.porcentajePorCargo["Team Lead"])
        assertEquals(null, highlights.porcentajePorCargo["Junior"])
    }

    @Test
    fun `agrupa highlights por pais`() {
        val highlightsPorPais = analytics.highlightsPorPais()
        assertEquals(6, highlightsPorPais.size)
    }

    @Test
    fun `calcula highlights por pais`() {
        val highlightsPorPais = analytics.highlightsPorPais()

        val highlightsPeru = highlightsPorPais["Perú"]
        checkNotNull(highlightsPeru)

        assertEquals(1_400.0, highlightsPeru.minMonedaLocal)
        assertEquals(16_500.0, highlightsPeru.maxMonedaLocal)
        assertEquals(7_000.0, highlightsPeru.medianMonedaLocal)
        assertEquals(2_300.0, highlightsPeru.minUSD)
        assertEquals(15_000.0, highlightsPeru.maxUSD)
        assertEquals(3500.0, highlightsPeru.medianUSD)
        assertEquals(listOf("Rappi", "CSTI", "Delivery Hero"), highlightsPeru.topEmpresasMonedaLocal)
        assertEquals(listOf("MT Llc", "Intive", "Metafy (antes)"), highlightsPeru.topEmpresasUSD)
    }

    @Nested
    @DisplayName("Calcula highlights por moneda")
    inner class HighlightsPorMoneda {
        val highlightsPorMoneda = analytics.highlightsPorMoneda()

        @Test
        fun `calcula minimo USD entre todos los registros`() {
            assertEquals(1_350.0, highlightsPorMoneda.minUSD)
        }

        @Test
        fun `calcula maximo USD entre todos los registros`() {
            assertEquals(15_000.0, highlightsPorMoneda.maxUSD)
        }

        @Test
        fun `calcula cuanto mas porcentaja ganan en USD`() {
            assertEquals(65, highlightsPorMoneda.cuantoMasPorcentajeGanasEnUSD)
        }

        @Test
        fun `calcula cuanto mas bajo en promedio es el salario local comparado a USD`() {
            assertEquals(mapOf(
                Moneda.PEN to 153,
                Moneda.COP to 61,
                Moneda.CLP to 40,
                Moneda.MXN to 44,
            ), highlightsPorMoneda.promedioEnElQueSalarioLocalEsMasBajoQueUSD)
        }

        @Test
        fun `calcula porcentaje de personas que trabajan remote y ganan en USD`() {
            assertEquals(97, highlightsPorMoneda.porcentajeGananEnDolaresTrabajanRemote)
        }

    }



}