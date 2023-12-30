import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import pe.dev.android.Analytics
import pe.dev.android.models.Moneda

class AnalyticsTest {

    val analytics = Analytics(testEntries)

    @Nested
    @DisplayName("Calcula highlights generales")
    inner class Highlights {

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
            assertEquals(17.0, highlights.porcentajePorCargo["Semi/Mid"])
            assertEquals(74.0, highlights.porcentajePorCargo["Senior"])
            assertEquals(3.0, highlights.porcentajePorCargo["Contractor"])
            assertEquals(6.0, highlights.porcentajePorCargo["Tech Lead"])
            assertEquals(null, highlights.porcentajePorCargo["Junior"])
        }

        @Test
        fun `calcula porcentaje por modalidad`() {
            val highlights = analytics.highlights()
            assertEquals(82.0, highlights.porcentajePorModalidad["Remoto"])
            assertEquals(5.0, highlights.porcentajePorModalidad["Presencial"])
            assertEquals(14.0, highlights.porcentajePorModalidad["Híbrido"])
        }
    }

    @Nested
    @DisplayName("Calcula highlights por pais")
    inner class HighlightsPorPais {
        val highlightsPorPais = analytics.highlightsPorPais()
        val highlightsPeru by lazy { highlightsPorPais["Perú"] }

        @Test
        fun `agrupa highlights por pais`() {
            assertEquals(6, highlightsPorPais.size)
            assertNotNull(highlightsPeru)
        }

        @Test
        fun `calcula minimo salario en moneda local`() {
            assertEquals(1_400.0, highlightsPeru?.minMonedaLocal)
        }

        @Test
        fun `calcula maximo salario en moneda local`() {
            assertEquals(16_500.0, highlightsPeru?.maxMonedaLocal)
        }

        @Test
        fun `calcula mediana salario en moneda local`() {
            assertEquals(7_000.0, highlightsPeru?.medianMonedaLocal)
        }

        @Test
        fun `calcula minimo salario en USD`() {
            assertEquals(2_300.0, highlightsPeru?.minUSD)
        }

        @Test
        fun `calcula maximo salario en USD`() {
            assertEquals(15_000.0, highlightsPeru?.maxUSD)
        }

        @Test
        fun `calcula mediana salario en USD`() {
            assertEquals(3500.0, highlightsPeru?.medianUSD)
        }

        @Test
        fun `calcula top 3 empresas con salario mas alto en moneda local`() {
            assertEquals(listOf("Rappi", "CSTI", "Delivery Hero"), highlightsPeru?.topEmpresasMonedaLocal)
        }

        @Test
        fun `calcula top 3 empresas con salario mas alto en USD`() {
            assertEquals(listOf("MT Llc", "Intive", "Metafy (antes)"), highlightsPeru?.topEmpresasUSD)
        }
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
                Moneda.PEN to 154,
                Moneda.COP to 62,
                Moneda.CLP to 41,
                Moneda.MXN to 44,
            ), highlightsPorMoneda.promedioEnElQueSalarioLocalEsMasBajoQueUSD)
        }

        @Test
        fun `calcula porcentaje de personas que trabajan remote y ganan en USD`() {
            assertEquals(97, highlightsPorMoneda.porcentajeGananEnDolaresTrabajanRemote)
        }

    }

    @Nested
    @DisplayName("Calcula highlights por seniority")
    inner class HighlightsPorSeniority {
        val highlightsPorSeniority = analytics.highlightsPorSeniority()

        @Test
        fun `agrupa por seniority`() {
            assertNotNull(highlightsPorSeniority["Senior"])
            assertNotNull(highlightsPorSeniority["Semi/Mid"])
            assertNotNull(highlightsPorSeniority["Contractor"])
            assertNotNull(highlightsPorSeniority["Tech Lead"])
            assertNotNull(highlightsPorSeniority["Junior"])
            assertEquals(5, highlightsPorSeniority.size)
        }

        @Test
        fun `calcula porcentaje comparado a otros seniorities`() {
            assertEquals(mapOf(
                "Semi/Mid" to -51,
                "Contractor" to 18,
                "Junior" to -82,
                "Tech Lead" to 9,
            ), highlightsPorSeniority["Senior"]?.porcentajeComparadoAOtrosSeniorities)
        }

        @Test
        fun `calcula minimo salario normalizado`() {
            assertEquals(1300.0, highlightsPorSeniority["Senior"]?.minNormalizado)
        }

        @Test
        fun `calcula maximo salario normalizado`() {
            assertEquals(15_000.0, highlightsPorSeniority["Senior"]?.maxNormalizado)
        }
    }


}