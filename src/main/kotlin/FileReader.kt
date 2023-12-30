package pe.dev.android

import pe.dev.android.models.Entry
import pe.dev.android.models.Moneda
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object FileReader {

    fun from(fileName: String): List<Entry> {
        val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream(fileName)
            ?: return emptyList()

        val reader = BufferedReader(InputStreamReader(inputStream))
        val lines = reader.readLines()
        return lines.drop(1).map {
            val components = it.split("\t")
            Entry(
                timestamp = components[0],
                genero = components[2],
                pais = components[3],
                anosDeExperiencia = components[4],
                cargo = components[5],
                empresa = components[6],
                modalidad = components[7],
                tecnologias = components[8].split(", "),
                moneda = Moneda.valueOf(components[9]),
                salario = components[10].toDouble(),
            )
        }
    }
}