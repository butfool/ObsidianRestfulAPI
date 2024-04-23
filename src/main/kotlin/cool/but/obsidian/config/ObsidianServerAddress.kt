package cool.but.obsidian.config

import com.dtflys.forest.callback.AddressSource
import com.dtflys.forest.http.ForestAddress
import com.dtflys.forest.http.ForestRequest
import org.springframework.context.annotation.Configuration

@Configuration
class ObsidianServerAddress(
    private val obsidianConfiguration: ObsidianConfiguration
) : AddressSource {
    override fun getAddress(p0: ForestRequest<*>?): ForestAddress {
        return ForestAddress("https", obsidianConfiguration.host, obsidianConfiguration.port)
    }
}