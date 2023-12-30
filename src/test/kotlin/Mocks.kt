import pe.dev.android.Entry
import pe.dev.android.EntryReader
import pe.dev.android.Moneda

val testEntries = EntryReader.from("salarios-dic29.tsv")

val testEntry = testEntries.first()