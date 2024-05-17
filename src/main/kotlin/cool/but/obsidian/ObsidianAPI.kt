package cool.but.obsidian

import com.dtflys.forest.http.ForestResponse
import com.fasterxml.jackson.databind.JsonNode
import cool.but.kt.common.utils.AssertUtils
import cool.but.obsidian.api.ObsidianDocumentClient
import cool.but.obsidian.config.ObsidianConfiguration
import cool.but.obsidian.cool.but.kt.common.annotations.Slf4j.Companion.logger
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
            path = markdownFile.path!!.ensureEndWithMd(),
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
            this.path = path
            this.name = path.substring(lastIndex + 1, path.length)
        }
    }

    /**
     * 将文件从 oldPath 移动到 newPath
     */
    fun moveDocument(old: String, new: String) {
        val oldPath = old.ensureEndWithMd()
        val newPath = new.ensureEndWithMd()
        if (oldPath == newPath) {
            logger.debug("Path are same, skip: {}", oldPath)
            return
        }
        AssertUtils.throwIf(oldPath.length < 3, ObsidianException("文件名长度小于 3：$oldPath"))
        val oldResult = getDocument(oldPath)
        AssertUtils.throwIfNot(oldResult.first, ObsidianException("没有找到源文件"))

        val newResult = getDocument(newPath)
        AssertUtils.throwIf(newResult.first, ObsidianException("目标路径不为空"))

        obsidianDocumentClient.createDocument(
            path = newPath.ensureEndWithMd(),
            content = oldResult.second,
            headers = obsidianConfiguration.headersMap
        )

        obsidianDocumentClient.deleteDocument(
            path = oldPath.ensureEndWithMd(),
            headers = obsidianConfiguration.headersMap
        )
    }

    fun getDocumentByFileName(filename: String, propertiesClass: Class<out MarkdownProperties>): Pair<Boolean, MarkdownFile> {
        AssertUtils.throwIf(filename.length < 3, ObsidianException("文件名长度小于 3：$filename"))
        val searchResult = searchDocumentByFileName(filename)
        return getDocument(searchResult.sortedBy { it.score }.last().filename!!, propertiesClass)
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
        var realPath = if (path.startsWith("/")) {
            path.substring(1)
        } else {
            path
        }
        realPath = if (realPath.endsWith("/")) {
            realPath
        } else {
            "$realPath/"
        }
        val response = obsidianDocumentClient.listDocumentsByPath(realPath, obsidianConfiguration.headersMap)
        return if (response.isSuccess) {
            response.result
        } else {
            ListDocumentByPathResult().apply {
                files = emptyList()
            }
        }
    }
}