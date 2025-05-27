package com.remotearmz.commandcenter.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarEntry
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineEntry
import com.remotearmz.commandcenter.R
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    DashboardScreen()
                }
            }
        }
    }

    @Composable
    fun DashboardScreen() {
        val outreachStats by viewModel.outreachStats.collectAsState()
        val targetStats by viewModel.targetStats.collectAsState()
        val clientStats by viewModel.clientStats.collectAsState()
        val leadStats by viewModel.leadStats.collectAsState()
        val recentActivity by viewModel.recentActivity.collectAsState(initial = emptyList())
        val upcomingTasks by viewModel.upcomingTasks.collectAsState(initial = emptyList())
        val overdueTasks by viewModel.overdueTasks.collectAsState(initial = emptyList())

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard") },
                    actions = {
                        IconButton(onClick = { viewModel.refreshData() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Date Range Selector
                DateRangeSelector(
                    dateRange = viewModel.dateRange.value,
                    onDateRangeSelected = { viewModel.setDateRange(it) },
                    onCustomDateSelected = { start, end ->
                        viewModel.setCustomDateRange(start, end)
                    }
                )

                // KPI Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    KPICard(
                        title = "Conversion Rate",
                        value = "${"%.1f".format(outreachStats.conversionRate)}%",
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF10B981)
                    )
                    KPICard(
                        title = "Target Completion",
                        value = "${"%.1f".format(targetStats.completionRate)}%",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF3B82F6)
                    )
                    KPICard(
                        title = "Active Clients",
                        value = clientStats.activeClients.toString(),
                        icon = Icons.Default.Business,
                        color = Color(0xFFF59E0B)
                    )
                    KPICard(
                        title = "Lead Conversion",
                        value = "${"%.1f".format(leadStats.conversionRate)}%",
                        icon = Icons.Default.PersonAdd,
                        color = Color(0xFFEF4444)
                    )
                }

                // Outreach Performance
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Outreach Performance",
                            style = MaterialTheme.typography.h6
                        )
                        BarChart(
                            entries = outreachStats.outreachByType.entries.map { (type, count) ->
                                BarEntry(
                                    value = count.toFloat(),
                                    label = type.name,
                                    color = when (type) {
                                        OutreachType.EMAIL -> Color(0xFF10B981)
                                        OutreachType.PHONE_CALL -> Color(0xFF3B82F6)
                                        OutreachType.LINKEDIN_MESSAGE -> Color(0xFFC084FC)
                                        OutreachType.SOCIAL_MEDIA_POST -> Color(0xFFF59E0B)
                                        OutreachType.MEETING -> Color(0xFFF97316)
                                        OutreachType.OTHER -> Color(0xFF94A3B8)
                                    }
                                )
                            }
                        )
                    }
                }

                // Target Progress
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Target Progress",
                            style = MaterialTheme.typography.h6
                        )
                        LineChart(
                            entries = viewModel.targetRepository
                                .getTargetProgress()
                                .map { progress ->
                                    LineEntry(
                                        x = progress.date.dayOfYear.toFloat(),
                                        y = progress.progress.toFloat(),
                                        label = progress.date.format(DateTimeFormatter.ofPattern("dd MMM"))
                                    )
                                }
                        )
                    }
                }

                // Recent Activity
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Recent Activity",
                            style = MaterialTheme.typography.h6
                        )
                        recentActivity.forEach { activity ->
                            ActivityItem(activity)
                        }
                    }
                }

                // Upcoming Tasks
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Upcoming Tasks",
                            style = MaterialTheme.typography.h6
                        )
                        upcomingTasks.forEach { task ->
                            TaskItem(task)
                        }
                    }
                }

                // Overdue Tasks
                if (overdueTasks.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Overdue Tasks",
                                style = MaterialTheme.typography.h6,
                                color = Color(0xFFEF4444)
                            )
                            overdueTasks.forEach { task ->
                                TaskItem(task, overdue = true)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DateRangeSelector(
        dateRange: DateRange,
        onDateRangeSelected: (DateRange) -> Unit,
        onCustomDateSelected: (LocalDate, LocalDate) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateRangeButton(
                    text = "Today",
                    isSelected = dateRange == DateRange.Today,
                    onClick = { onDateRangeSelected(DateRange.Today) }
                )
                DateRangeButton(
                    text = "Week",
                    isSelected = dateRange == DateRange.Week,
                    onClick = { onDateRangeSelected(DateRange.Week) }
                )
                DateRangeButton(
                    text = "Month",
                    isSelected = dateRange == DateRange.Month,
                    onClick = { onDateRangeSelected(DateRange.Month) }
                )
                DateRangeButton(
                    text = "Year",
                    isSelected = dateRange == DateRange.Year,
                    onClick = { onDateRangeSelected(DateRange.Year) }
                )
                DateRangeButton(
                    text = "Custom",
                    isSelected = dateRange == DateRange.Custom,
                    onClick = { /* TODO: Show date picker */ }
                )
            }
        }
    }

    @Composable
    fun KPICard(
        title: String,
        value: String,
        icon: ImageVector,
        color: Color
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.h6,
                    color = color
                )
            }
        }
    }

    @Composable
    fun ActivityItem(activity: ActivityItem) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.body1
                )
                ActivityStatusChip(activity.status)
            }
        }
    }

    @Composable
    fun TaskItem(task: Task, overdue: Boolean = false) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        text = task.dueDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        style = MaterialTheme.typography.caption
                    )
                }
                TaskStatusChip(task.status, overdue)
            }
        }
    }

    @Composable
    fun ActivityStatusChip(status: ActivityStatus) {
        val color = when (status) {
            ActivityStatus.COMPLETED -> Color(0xFF10B981)
            ActivityStatus.PENDING -> Color(0xFFFFD700)
            ActivityStatus.FAILED -> Color(0xFFEF4444)
            ActivityStatus.SCHEDULED -> Color(0xFF3B82F6)
        }

        Chip(
            colors = ChipDefaults.chipColors(backgroundColor = color),
            onClick = { /* TODO: Handle click */ }
        ) {
            Text(
                text = status.name,
                style = MaterialTheme.typography.caption,
                color = Color.White
            )
        }
    }

    @Composable
    fun TaskStatusChip(status: TaskStatus, overdue: Boolean) {
        val color = when {
            overdue -> Color(0xFFEF4444)
            status == TaskStatus.COMPLETED -> Color(0xFF10B981)
            status == TaskStatus.IN_PROGRESS -> Color(0xFF3B82F6)
            status == TaskStatus.PENDING -> Color(0xFFFFD700)
            status == TaskStatus.OVERDUE -> Color(0xFFEF4444)
            else -> Color(0xFF94A3B8)
        }

        Chip(
            colors = ChipDefaults.chipColors(backgroundColor = color),
            onClick = { /* TODO: Handle click */ }
        ) {
            Text(
                text = status.name,
                style = MaterialTheme.typography.caption,
                color = Color.White
            )
        }
    }

    @Composable
    fun DateRangeButton(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isSelected) Color(0xFF10B981) else MaterialTheme.colors.primary
            )
        ) {
            Text(text)
        }
    }
}
