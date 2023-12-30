import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pe.dev.android.Moneda

class EntryTest {

    @Test
    fun `calcula tipo de cambio de PEN a USD`() {
        val pen = testEntry.copy(
            moneda = Moneda.PEN,
            salario = 1000.0,
        )
        assertEquals(270.0, pen.salarioNormalizado())
    }

    @Test
    fun `calcula tipo de cambio de USD a USD`() {
        val usd = testEntry.copy(
            moneda = Moneda.USD,
            salario = 1000.0,
        )
        assertEquals(1000.0, usd.salarioNormalizado())
    }

    @Test
    fun `calcula tipo de cambio de COP a USD`() {
        val cop = testEntry.copy(
            moneda = Moneda.COP,
            salario = 1_000_000.0,
        )
        assertEquals(260.0, cop.salarioNormalizado())
    }

    @Test
    fun `calcula tipo de cambio de MXN a USD`() {
        val mxn = testEntry.copy(
            moneda = Moneda.MXN,
            salario = 100_000.0,
        )
        assertEquals(5900.0, mxn.salarioNormalizado())
    }

    @Test
    fun `calcula tipo de cambio de CLP a USD`() {
        val clp = testEntry.copy(
            moneda = Moneda.CLP,
            salario = 1_000_000.0,
        )
        assertEquals(1100.0, clp.salarioNormalizado())
    }
}