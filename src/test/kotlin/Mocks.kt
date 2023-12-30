import pe.dev.android.FileReader

val testEntries = FileReader.from("salarios-dic29.tsv")

val testEntry = testEntries.first()