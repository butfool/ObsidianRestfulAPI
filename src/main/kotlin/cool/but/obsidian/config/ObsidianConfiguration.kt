package cool.but.obsidian.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("obsidian")
class ObsidianConfiguration {

    var host: String = "localhost"

    var port: Int = 27124

    var token: String? = null

    val headersMap: Map<String, String> by lazy {
        mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "text/markdown",
        )
    }

}