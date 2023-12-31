package pe.dev.android

fun main() {
    val entries = FileReader.from("salarios-dic29.tsv")
    val analytics =  Analytics(entries)
    val display = AnalyticsConsole()

    display.display(analytics.highlights())
    display.display(analytics.highlightsPorMoneda())
    display.display(analytics.highlightsPorPais())
    display.displayBySeniority(analytics.highlightsPorSeniority())
}

