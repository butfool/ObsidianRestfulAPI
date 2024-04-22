package cool.but.obsidian.api

import com.dtflys.forest.annotation.Address
import com.dtflys.forest.annotation.Body
import com.dtflys.forest.annotation.Header
import com.dtflys.forest.annotation.Put
import com.dtflys.forest.annotation.Var
import com.dtflys.forest.http.ForestResponse
import com.fasterxml.jackson.databind.JsonNode
import cool.but.obsidian.config.ObsidianServerAddress
import org.springframework.stereotype.Component

@Component
interface ObsidianDocumentClient {

    @Put("/vault/{filename}.md")
    @Address(source = ObsidianServerAddress::class)
    fun createDocument(
        @Var("filename") filename: String,
        @Body content: String,
        @Header headers: Map<String, String>,
    ): ForestResponse<JsonNode>

}


