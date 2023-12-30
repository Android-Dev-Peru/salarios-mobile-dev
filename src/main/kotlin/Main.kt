package pe.dev.android

fun main() {
    val entries = EntryReader.from("salarios-dic29.tsv")
    val analytics =  Analytics(entries)
    val display = AnalyticsConsole()

    display.display(analytics.highlights())
}
