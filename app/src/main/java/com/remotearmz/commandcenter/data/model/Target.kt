package com.remotearmz.commandcenter.data.model

import java.time.LocalDate

enum class TargetType {
    WEEKLY,
    MONTHLY,
    ANNUAL
}

enum class TargetStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE
}

data class Target(
    val id: String,
    val title: String,
    val targetType: TargetType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val targetValue: Double,
    val currentProgress: Double = 0.0,
    val unit: String,
    val description: String? = null,
    val status: TargetStatus = TargetStatus.PENDING,
    val category: String,
    val priority: Int = 1,
    val createdAt: LocalDate = LocalDate.now(),
    val updatedAt: LocalDate = LocalDate.now(),
    val iconName: String
)

data class TargetProgress(
    val date: LocalDate,
    val progress: Double,
    val notes: String? = null
)

data class TargetCategory(
    val id: String,
    val name: String,
    val description: String? = null,
    val iconName: String
)
