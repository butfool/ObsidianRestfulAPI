package cool.but.obsidian

import com.dtflys.forest.http.ForestResponse
import com.fasterxml.jackson.databind.JsonNode
import cool.but.kt.common.utils.AssertUtils
import cool.but.obsidian.api.ObsidianDocumentClient
import cool.but.obsidian.config.ObsidianConfiguration
import cool.but.obsidian.data.dto.ListDocumentByPathResult
import cool.but.obsidian.data.dto.SimpleSearchResult
import cool.but.obsidian.entity.MarkdownFile
import cool.but.obsidian.entity.MarkdownProperties
import cool.but.obsidian.exception.ObsidianException
import cool.but.obsidian.utils.ensureEndWithMd
import org.springframework.stereotype.Service

@Service
class ObsidianAPI(
    private val obsidianDocumentClient: ObsidianDocumentClient,
    private val obsidianConfiguration: ObsidianConfiguration,
) {

    /**
     *  有则更新，无则创建
     */
    fun upsertDocument(markdownFile: MarkdownFile): ForestResponse<JsonNode> {
        val name = markdownFile.name!!.ensureEndWithMd()
        return obsidianDocumentClient.createDocument(
            path = "${markdownFile.path!!}/$name",
            content = markdownFile.toString(),
            headers = obsidianConfiguration.headersMap
        )
    }

    /**
     * 根据路径获取文件，直接返回 String
     */
    fun getDocument(path: String): Pair<Boolean, String> {
        val response = obsidianDocumentClient.getDocument(
            path = path.ensureEndWithMd(),
            headers = obsidianConfiguration.headersMap
        )
        return (response.statusCode == 200) to response.content
    }

    /**
     * 根据路径获取文件，返回 MarkdownFile
     */
    fun getDocument(path: String, propertiesClass: Class<out MarkdownProperties>): Pair<Boolean, MarkdownFile> {
        val ret = getDocument(path)
        return ret.first to MarkdownFile.fromString(ret.second, propertiesClass).apply {
            // 文件路径
            val lastIndex = path.lastIndexOf('/')
            this.path = path.substring(0, lastIndex)
            this.name = path.substring(lastIndex + 1, path.length)
        }
    }

    /**
     * 将文件从 oldPath 移动到 newPath
     */
    fun moveDocument(oldPath: String, newPath: String) {
        AssertUtils.throwIf(oldPath.length < 3, ObsidianException("文件名长度小于 3：$oldPath"))
        val oldResult = getDocument(oldPath)
        AssertUtils.throwIfNot(oldResult.first, ObsidianException("没有找到源文件"))

        val newResult = getDocument(newPath)
        AssertUtils.throwIf(newResult.first, ObsidianException("目标路径不为空"))

        obsidianDocumentClient.createDocument(
            path = newPath,
            content = oldResult.second,
            headers = obsidianConfiguration.headersMap
        )

        obsidianDocumentClient.deleteDocument(
            path = oldPath,
            headers = obsidianConfiguration.headersMap
        )
    }

    fun getDocumentByFileName(filename: String, propertiesClass: Class<out MarkdownProperties>): Pair<Boolean, MarkdownFile> {
        AssertUtils.throwIf(filename.length < 3, ObsidianException("文件名长度小于 3：$filename"))
        val searchResult = searchDocumentByFileName(filename)
        AssertUtils.throwIf(searchResult.size != 1, ObsidianException("文件数量不为 1：${searchResult.size}"))
        return getDocument(searchResult[0].filename!!, propertiesClass)
    }


    fun searchDocumentByFileName(filename: String): List<SimpleSearchResult> {
        return obsidianDocumentClient.searchDocumentByText(filename, headers = obsidianConfiguration.headersMap)
    }

    fun listDocumentByPath(path: String): ListDocumentByPathResult {
        if (path.isBlank()) {
            return ListDocumentByPathResult().apply {
                files = emptyList()
            }
        }
        val realPath = if (path.startsWith("/")) {
            path.substring(1)
        } else {
            path
        }
        return obsidianDocumentClient.listDocumentsByPath(realPath, obsidianConfiguration.headersMap)
    }
}