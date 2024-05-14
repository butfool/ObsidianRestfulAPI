package cool.but.obsidian.utils

fun String.ensureEndWithMd(): String {
    return if (this.endsWith(".md")) {
        this
    } else {
        "$this.md"
    }
}