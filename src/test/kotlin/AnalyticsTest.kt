import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pe.dev.android.Analytics
import pe.dev.android.EntryReader

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


}