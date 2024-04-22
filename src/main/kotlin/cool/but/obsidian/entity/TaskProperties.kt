package cool.but.iwhale.data.obsidian

import java.util.Date

class TaskProperties : MarkdownProperties {
    // 任务本身属性
    var taskId: Int? = null
    var taskNo: Int? = null
    var taskTitle: String? = null

    // 时间相关
    var createDate: String? = null
    var stateDate: String? = null

    // 状态
    var isBlocked: String? = null
    var status: String? = null
    var isVersioned: String? = null

    // 关联用户
    var currentHandler: String? = null

    // Obsidian 任务属性
    var processStartDate: Date? = null
    var processEndDate: Date? = null
}