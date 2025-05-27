package com.remotearmz.commandcenter.data.repository

import com.remotearmz.commandcenter.data.model.Outreach
import com.remotearmz.commandcenter.data.model.OutreachAnalytics
import com.remotearmz.commandcenter.data.model.OutreachAnalyticsSummary
import com.remotearmz.commandcenter.data.model.OutreachStatus
import com.remotearmz.commandcenter.data.model.OutreachType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

interface OutreachAnalyticsRepository {
    suspend fun getDailyAnalytics(date: LocalDate): OutreachAnalytics
    suspend fun getWeeklyAnalytics(): OutreachAnalyticsSummary
    suspend fun getMonthlyAnalytics(): OutreachAnalyticsSummary
    suspend fun getAnalyticsByType(type: OutreachType): List<OutreachAnalytics>
    suspend fun getAnalyticsByStatus(status: OutreachStatus): List<OutreachAnalytics>
    suspend fun getConversionRate(): Double
    suspend fun getAverageResponseTime(): Long
}

class DefaultOutreachAnalyticsRepository(
    private val outreachRepository: OutreachRepository
) : OutreachAnalyticsRepository {
    override suspend fun getDailyAnalytics(date: LocalDate): OutreachAnalytics {
        val startOfDay = date.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
        val endOfDay = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))

        val outreachList = outreachRepository.getOutreachByDateRange(startOfDay, endOfDay).first()
        return calculateAnalytics(outreachList)
    }

    override suspend fun getWeeklyAnalytics(): OutreachAnalyticsSummary {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(7)
        val endOfWeek = today.plusDays(1)

        val weeklyTrend = (0..7).map { days ->
            val date = today.minusDays(days)
            getDailyAnalytics(date)
        }

        val outreachList = outreachRepository.getOutreachByDateRange(
            startOfWeek.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
            endOfWeek.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
        ).first()

        return OutreachAnalyticsSummary(
            totalOutreach = outreachList.size,
            successfulOutreach = outreachList.count { it.status == OutreachStatus.COMPLETED },
            conversionRate = calculateConversionRate(outreachList),
            averageResponseTime = calculateAverageResponseTime(outreachList),
            outreachByType = outreachList.groupingBy { it.outreachType }.eachCount(),
            outreachByStatus = outreachList.groupingBy { it.status }.eachCount(),
            topPerformingType = findTopPerformingType(outreachList),
            leastPerformingType = findLeastPerformingType(outreachList),
            weeklyTrend = weeklyTrend,
            monthlyTrend = getMonthlyTrend()
        )
    }

    override suspend fun getMonthlyAnalytics(): OutreachAnalyticsSummary {
        val today = LocalDate.now()
        val startOfMonth = today.minusMonths(1)
        val endOfMonth = today.plusDays(1)

        val monthlyTrend = (0..30).map { days ->
            val date = today.minusDays(days)
            getDailyAnalytics(date)
        }

        val outreachList = outreachRepository.getOutreachByDateRange(
            startOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
            endOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
        ).first()

        return OutreachAnalyticsSummary(
            totalOutreach = outreachList.size,
            successfulOutreach = outreachList.count { it.status == OutreachStatus.COMPLETED },
            conversionRate = calculateConversionRate(outreachList),
            averageResponseTime = calculateAverageResponseTime(outreachList),
            outreachByType = outreachList.groupingBy { it.outreachType }.eachCount(),
            outreachByStatus = outreachList.groupingBy { it.status }.eachCount(),
            topPerformingType = findTopPerformingType(outreachList),
            leastPerformingType = findLeastPerformingType(outreachList),
            weeklyTrend = getWeeklyTrend(),
            monthlyTrend = monthlyTrend
        )
    }

    override suspend fun getAnalyticsByType(type: OutreachType): List<OutreachAnalytics> {
        val today = LocalDate.now()
        val startOfMonth = today.minusMonths(1)
        val endOfMonth = today.plusDays(1)

        val outreachList = outreachRepository.getOutreachByDateRange(
            startOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
            endOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
        ).first()

        return (0..30).map { days ->
            val date = today.minusDays(days)
            val outreachForDate = outreachList.filter {
                val outreachDate = LocalDate.ofEpochDay(it.outreachDate / 86400)
                outreachDate == date && it.outreachType == type
            }
            OutreachAnalytics(
                date = date,
                outreachCount = outreachForDate.size,
                outreachByType = mapOf(type to outreachForDate.size),
                outreachByStatus = outreachForDate.groupingBy { it.status }.eachCount(),
                conversionRate = calculateConversionRate(outreachForDate),
                averageResponseTime = calculateAverageResponseTime(outreachForDate),
                topPerformingType = type,
                leastPerformingType = type,
                successfulOutreach = outreachForDate.count { it.status == OutreachStatus.COMPLETED },
                totalOutreach = outreachForDate.size
            )
        }
    }

    override suspend fun getAnalyticsByStatus(status: OutreachStatus): List<OutreachAnalytics> {
        val today = LocalDate.now()
        val startOfMonth = today.minusMonths(1)
        val endOfMonth = today.plusDays(1)

        val outreachList = outreachRepository.getOutreachByDateRange(
            startOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
            endOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
        ).first()

        return (0..30).map { days ->
            val date = today.minusDays(days)
            val outreachForDate = outreachList.filter {
                val outreachDate = LocalDate.ofEpochDay(it.outreachDate / 86400)
                outreachDate == date && it.status == status
            }
            OutreachAnalytics(
                date = date,
                outreachCount = outreachForDate.size,
                outreachByType = outreachForDate.groupingBy { it.outreachType }.eachCount(),
                outreachByStatus = mapOf(status to outreachForDate.size),
                conversionRate = if (status == OutreachStatus.COMPLETED) 100.0 else 0.0,
                averageResponseTime = calculateAverageResponseTime(outreachForDate),
                topPerformingType = findTopPerformingType(outreachForDate),
                leastPerformingType = findLeastPerformingType(outreachForDate),
                successfulOutreach = outreachForDate.count { it.status == OutreachStatus.COMPLETED },
                totalOutreach = outreachForDate.size
            )
        }
    }

    override suspend fun getConversionRate(): Double {
        val today = LocalDate.now()
        val startOfMonth = today.minusMonths(1)
        val endOfMonth = today.plusDays(1)

        val outreachList = outreachRepository.getOutreachByDateRange(
            startOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
            endOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
        ).first()

        return calculateConversionRate(outreachList)
    }

    override suspend fun getAverageResponseTime(): Long {
        val today = LocalDate.now()
        val startOfMonth = today.minusMonths(1)
        val endOfMonth = today.plusDays(1)

        val outreachList = outreachRepository.getOutreachByDateRange(
            startOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())),
            endOfMonth.atStartOfDay().toEpochSecond(ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()))
        ).first()

        return calculateAverageResponseTime(outreachList)
    }

    private fun calculateAnalytics(outreachList: List<Outreach>): OutreachAnalytics {
        val date = LocalDate.now()
        return OutreachAnalytics(
            date = date,
            outreachCount = outreachList.size,
            outreachByType = outreachList.groupingBy { it.outreachType }.eachCount(),
            outreachByStatus = outreachList.groupingBy { it.status }.eachCount(),
            conversionRate = calculateConversionRate(outreachList),
            averageResponseTime = calculateAverageResponseTime(outreachList),
            topPerformingType = findTopPerformingType(outreachList),
            leastPerformingType = findLeastPerformingType(outreachList),
            successfulOutreach = outreachList.count { it.status == OutreachStatus.COMPLETED },
            totalOutreach = outreachList.size
        )
    }

    private fun calculateConversionRate(outreachList: List<Outreach>): Double {
        val total = outreachList.size
        val successful = outreachList.count { it.status == OutreachStatus.COMPLETED }
        return if (total == 0) 0.0 else (successful.toDouble() / total) * 100
    }

    private fun calculateAverageResponseTime(outreachList: List<Outreach>): Long {
        val completedOutreach = outreachList.filter { it.status == OutreachStatus.COMPLETED }
        return if (completedOutreach.isEmpty()) 0 else
            completedOutreach.sumOf { it.outreachDate - it.createdAt } / completedOutreach.size
    }

    private fun findTopPerformingType(outreachList: List<Outreach>): OutreachType {
        return outreachList.groupingBy { it.outreachType }
            .eachCount()
            .maxByOrNull { it.value }?.key ?: OutreachType.EMAIL
    }

    private fun findLeastPerformingType(outreachList: List<Outreach>): OutreachType {
        return outreachList.groupingBy { it.outreachType }
            .eachCount()
            .minByOrNull { it.value }?.key ?: OutreachType.EMAIL
    }

    private fun getWeeklyTrend(): List<OutreachAnalytics> {
        val today = LocalDate.now()
        return (0..7).map { days ->
            val date = today.minusDays(days)
            getDailyAnalytics(date)
        }
    }

    private fun getMonthlyTrend(): List<OutreachAnalytics> {
        val today = LocalDate.now()
        return (0..30).map { days ->
            val date = today.minusDays(days)
            getDailyAnalytics(date)
        }
    }
}
