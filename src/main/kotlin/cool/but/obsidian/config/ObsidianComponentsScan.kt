package cool.but.obsidian.config

import com.dtflys.forest.springboot.annotation.ForestScan
import org.springframework.context.annotation.ComponentScan

@ComponentScan("cool.but.obsidian")
@ForestScan("cool.but.obsidian.api")
class ObsidianComponentsScan