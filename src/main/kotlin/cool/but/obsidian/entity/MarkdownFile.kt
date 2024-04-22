package cool.but.obsidian.entity

import cool.but.iwhale.data.obsidian.MarkdownProperties
import org.yaml.snakeyaml.Yaml

data class MarkdownFile(
    var path: String? = null,
    var name: String? = null,
    var properties: MarkdownProperties? = null,
    var content: String? = null,
) {
    override fun toString(): String {
        return StringBuilder().apply {
            if (properties != null) {
                append("---\n")
                append(Yaml().dump(properties))
                append("---\n\n")
            }
            append(content)
        }.toString()
    }
}