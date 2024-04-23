package cool.but.obsidian

import cool.but.obsidian.api.ObsidianDocumentClient
import cool.but.obsidian.config.ObsidianConfiguration
import cool.but.obsidian.entity.MarkdownFile
import org.springframework.context.annotation.ComponentScan
import org.springframework.stereotype.Service

@Service
class ObsidianAPI(
    private val obsidianDocumentClient: ObsidianDocumentClient,
    private val obsidianConfiguration: ObsidianConfiguration,
) {

    fun upsertDocument(markdownFile: MarkdownFile) {
        obsidianDocumentClient.createDocument(
            filename = "${markdownFile.path!!}/${markdownFile.name}",
            content = markdownFile.toString(),
            headers = obsidianConfiguration.headersMap
        )
    }
}