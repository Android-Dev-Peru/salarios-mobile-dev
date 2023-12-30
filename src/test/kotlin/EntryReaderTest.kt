import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pe.dev.android.EntryReader
import pe.dev.android.Moneda

class EntryReaderTest {

    @Test
    fun `omits header and reads all entries from file`() {
        val entries = EntryReader.from("salarios-dic29.tsv")
        assertEquals(109, entries.size)
    }

    @Test
    fun `mapea todos los campos`() {
        val entries = EntryReader.from("salarios-dic29.tsv")
        val firstEntry = entries.first()
        assertEquals("12/1/2023 12:46:48", firstEntry.timestamp)
        assertEquals("Másculino", firstEntry.genero)
        assertEquals("Perú", firstEntry.pais)
        assertEquals("5", firstEntry.anosDeExperiencia)
        assertEquals("Semi/Mid", firstEntry.cargo)
        assertEquals("Encora", firstEntry.empresa)
        assertEquals("Híbrido", firstEntry.modalidad)
        assertEquals(listOf("Android"), firstEntry.tecnologias)
        assertEquals(Moneda.PEN, firstEntry.moneda)
        assertEquals(7000.0, firstEntry.salario)
    }

    @Test
    fun `separa tecnologias por coma`() {
        val entries = EntryReader.from("salarios-dic29.tsv")
        val firstEntry = entries[9]
        assertEquals(listOf("Flutter", "Android"), firstEntry.tecnologias)
    }

    @Test
    fun `mapea todas las monedas`() {
        val entries = EntryReader.from("salarios-dic29.tsv")
        assertEquals(Moneda.PEN, entries[0].moneda)
        assertEquals(Moneda.USD, entries[2].moneda)
        assertEquals(Moneda.CLP, entries[104].moneda)
        assertEquals(Moneda.COP, entries[40].moneda)
        assertEquals(Moneda.MXN, entries[42].moneda)
    }
}