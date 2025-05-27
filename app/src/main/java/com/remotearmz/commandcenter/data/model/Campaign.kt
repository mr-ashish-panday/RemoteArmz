package com.remotearmz.commandcenter.data.model

import java.time.Instant

enum class CampaignStatus {
    DRAFT,
    ACTIVE,
    PAUSED,
    COMPLETED,
    ARCHIVED,
    CANCELLED
}

enum class CampaignType {
    EMAIL,
    MESSAGE,
    PHONE,
    SOCIAL,
    MULTI_CHANNEL
}

enum class CampaignPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

data class Campaign(
    val id: String,
    val name: String,
    val description: String,
    val type: CampaignType,
    val status: CampaignStatus,
    val priority: CampaignPriority,
    val targetCount: Int,
    val sentCount: Int,
    val responseCount: Int,
    val conversionCount: Int,
    val startDate: Instant,
    val endDate: Instant?,
    val budget: Double?,
    val templates: List<CampaignTemplate>,
    val segments: List<CampaignSegment>,
    val schedule: CampaignSchedule?,
    val createdBy: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val metadata: Map<String, Any> = emptyMap()
)

data class CampaignTemplate(
    val id: String,
    val name: String,
    val type: CampaignType,
    val content: String,
    val variables: Map<String, String>,
    val order: Int,
    val delay: Int?,
    val conditions: List<CampaignCondition>
)

data class CampaignSegment(
    val id: String,
    val name: String,
    val filters: List<CampaignFilter>,
    val size: Int,
    val templateIds: List<String>
)

data class CampaignSchedule(
    val type: ScheduleType,
    val startTime: Instant,
    val endTime: Instant?,
    val frequency: ScheduleFrequency?,
    val daysOfWeek: List<DayOfWeek>?,
    val timeOfDay: String?
)

enum class ScheduleType {
    ONCE,
    DAILY,
    WEEKLY,
    MONTHLY,
    CUSTOM
}

enum class ScheduleFrequency {
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY
}

enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}

data class CampaignCondition(
    val type: ConditionType,
    val field: String,
    val operator: ConditionOperator,
    val value: Any
)

enum class ConditionType {
    TARGET,
    RESPONSE,
    ACTION,
    TIME,
    CUSTOM
}

enum class ConditionOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    CONTAINS,
    NOT_CONTAINS,
    IN,
    NOT_IN
}

data class CampaignFilter(
    val type: FilterType,
    val field: String,
    val operator: FilterOperator,
    val value: Any
)

enum class FilterType {
    DEMOGRAPHIC,
    BEHAVIORAL,
    ENGAGEMENT,
    CUSTOM
}

enum class FilterOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    CONTAINS,
    NOT_CONTAINS,
    IN,
    NOT_IN
}

data class CampaignMetrics(
    val campaignId: String,
    val sent: Int,
    val delivered: Int,
    val opened: Int,
    val clicked: Int,
    val responded: Int,
    val converted: Int,
    val bounceRate: Double,
    val openRate: Double,
    val clickRate: Double,
    val responseRate: Double,
    val conversionRate: Double,
    val cost: Double,
    val roi: Double,
    val metadata: Map<String, Any> = emptyMap()
)
