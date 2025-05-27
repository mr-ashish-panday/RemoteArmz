package com.remotearmz.commandcenter.data.model

import java.time.LocalDate

data class OutreachAnalytics(
    val date: LocalDate,
    val outreachCount: Int,
    val outreachByType: Map<OutreachType, Int>,
    val outreachByStatus: Map<OutreachStatus, Int>,
    val conversionRate: Double,
    val averageResponseTime: Long, // in milliseconds
    val topPerformingType: OutreachType,
    val leastPerformingType: OutreachType,
    val successfulOutreach: Int,
    val totalOutreach: Int
)

data class OutreachAnalyticsSummary(
    val totalOutreach: Int,
    val successfulOutreach: Int,
    val conversionRate: Double,
    val averageResponseTime: Long,
    val outreachByType: Map<OutreachType, Int>,
    val outreachByStatus: Map<OutreachStatus, Int>,
    val topPerformingType: OutreachType,
    val leastPerformingType: OutreachType,
    val weeklyTrend: List<OutreachAnalytics>,
    val monthlyTrend: List<OutreachAnalytics>
)
