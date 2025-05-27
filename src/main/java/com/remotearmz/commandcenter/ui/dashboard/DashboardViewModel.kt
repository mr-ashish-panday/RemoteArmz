package com.remotearmz.commandcenter.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remotearmz.commandcenter.data.model.*
import com.remotearmz.commandcenter.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val outreachRepository: OutreachRepository,
    private val targetRepository: TargetRepository,
    private val clientRepository: ClientRepository,
    private val leadRepository: LeadRepository
) : ViewModel() {

    private val _dateRange = MutableStateFlow<DateRange>(DateRange.Today)
    val dateRange: StateFlow<DateRange> = _dateRange

    private val _startDate = MutableStateFlow(LocalDate.now())
    val startDate: StateFlow<LocalDate> = _startDate

    private val _endDate = MutableStateFlow(LocalDate.now())
    val endDate: StateFlow<LocalDate> = _endDate

    val outreachStats: StateFlow<OutreachStats> = outreachRepository
        .getOutreachStats(_startDate.value, _endDate.value)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OutreachStats()
        )

    val targetStats: StateFlow<TargetStats> = targetRepository
        .getTargetStats(_startDate.value, _endDate.value)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TargetStats()
        )

    val clientStats: StateFlow<ClientStats> = clientRepository
        .getClientStats(_startDate.value, _endDate.value)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ClientStats()
        )

    val leadStats: StateFlow<LeadStats> = leadRepository
        .getLeadStats(_startDate.value, _endDate.value)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LeadStats()
        )

    val recentActivity: Flow<List<ActivityItem>> = outreachRepository
        .getRecentActivity(_startDate.value, _endDate.value)

    val upcomingTasks: Flow<List<Task>> = outreachRepository
        .getUpcomingTasks(_startDate.value, _endDate.value)

    val overdueTasks: Flow<List<Task>> = outreachRepository
        .getOverdueTasks(_startDate.value, _endDate.value)

    fun setDateRange(range: DateRange) {
        _dateRange.value = range
        updateDateRange()
    }

    private fun updateDateRange() {
        val today = LocalDate.now()
        val range = _dateRange.value
        
        val newStartDate = when (range) {
            DateRange.Today -> today
            DateRange.Week -> today.minusDays(7)
            DateRange.Month -> today.minusMonths(1)
            DateRange.Year -> today.minusYears(1)
            DateRange.Custom -> _startDate.value
        }

        _startDate.value = newStartDate
        _endDate.value = today
    }

    fun setCustomDateRange(startDate: LocalDate, endDate: LocalDate) {
        _dateRange.value = DateRange.Custom
        _startDate.value = startDate
        _endDate.value = endDate
    }

    fun refreshData() {
        viewModelScope.launch {
            outreachRepository.refreshOutreachStats()
            targetRepository.refreshTargetStats()
            clientRepository.refreshClientStats()
            leadRepository.refreshLeadStats()
        }
    }

    fun markTaskAsCompleted(taskId: String) {
        viewModelScope.launch {
            outreachRepository.markTaskAsCompleted(taskId)
        }
    }

    fun markTaskAsOverdue(taskId: String) {
        viewModelScope.launch {
            outreachRepository.markTaskAsOverdue(taskId)
        }
    }
}

enum class DateRange {
    Today,
    Week,
    Month,
    Year,
    Custom
}

data class OutreachStats(
    val totalOutreach: Int = 0,
    val completedOutreach: Int = 0,
    val pendingOutreach: Int = 0,
    val conversionRate: Double = 0.0,
    val averageResponseTime: Long = 0,
    val outreachByType: Map<OutreachType, Int> = emptyMap(),
    val outreachByStatus: Map<OutreachStatus, Int> = emptyMap()
)

data class TargetStats(
    val totalTargets: Int = 0,
    val completedTargets: Int = 0,
    val overdueTargets: Int = 0,
    val inProgressTargets: Int = 0,
    val completionRate: Double = 0.0,
    val targetsByCategory: Map<String, Int> = emptyMap()
)

data class ClientStats(
    val totalClients: Int = 0,
    val activeClients: Int = 0,
    val inactiveClients: Int = 0,
    val totalRevenue: Double = 0.0,
    val averageRevenue: Double = 0.0
)

data class LeadStats(
    val totalLeads: Int = 0,
    val warmLeads: Int = 0,
    val coldLeads: Int = 0,
    val bookedLeads: Int = 0,
    val convertedLeads: Int = 0,
    val conversionRate: Double = 0.0
)

data class ActivityItem(
    val id: String,
    val type: ActivityType,
    val description: String,
    val timestamp: LocalDate,
    val status: ActivityStatus
)

enum class ActivityType {
    OUTREACH,
    TARGET,
    CLIENT,
    LEAD,
    TASK
}

enum class ActivityStatus {
    COMPLETED,
    PENDING,
    FAILED,
    SCHEDULED
}

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val status: TaskStatus,
    val priority: Int
)

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE
}
