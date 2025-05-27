package com.remotearmz.commandcenter.ui.analytics

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
import com.github.tehras.charts.pie.PieChart
import com.github.tehras.charts.pie.PieEntry
import com.remotearmz.commandcenter.R
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class OutreachDashboardFragment : Fragment() {
    private val viewModel: OutreachAnalyticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    OutreachDashboard()
                }
            }
        }
    }

    @Composable
    fun OutreachDashboard() {
        val dailyAnalytics by viewModel.dailyAnalytics.collectAsState()
        val weeklyAnalytics by viewModel.weeklyAnalytics.collectAsState()
        val monthlyAnalytics by viewModel.monthlyAnalytics.collectAsState()
        val analyticsByType by viewModel.analyticsByType.collectAsState()
        val conversionRate by viewModel.conversionRate.collectAsState()
        val averageResponseTime by viewModel.averageResponseTime.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Outreach Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                // Date Selector
                DateSelector { date ->
                    viewModel.getAnalyticsForDate(date)
                }

                // Key Metrics Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Key Metrics",
                            style = MaterialTheme.typography.h6
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MetricCard(
                                title = "Conversion Rate",
                                value = conversionRate?.let { "${"%.1f".format(it)}%" } ?: "-"
                            )
                            MetricCard(
                                title = "Avg Response Time",
                                value = averageResponseTime?.let { "${it / 1000 / 60} mins" } ?: "-"
                            )
                            MetricCard(
                                title = "Total Outreach",
                                value = dailyAnalytics?.totalOutreach?.toString() ?: "-"
                            )
                        }
                    }
                }

                // Outreach Type Distribution
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Outreach Type Distribution",
                            style = MaterialTheme.typography.h6
                        )
                        analyticsByType?.entries?.toList()?.let { entries ->
                            PieChart(
                                entries = entries.map { (type, analytics) ->
                                    PieEntry(
                                        value = analytics.sumOf { it.outreachCount }.toFloat(),
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
                }

                // Status Distribution
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Status Distribution",
                            style = MaterialTheme.typography.h6
                        )
                        dailyAnalytics?.outreachByStatus?.entries?.toList()?.let { entries ->
                            BarChart(
                                entries = entries.map { (status, count) ->
                                    BarEntry(
                                        value = count.toFloat(),
                                        label = status.name,
                                        color = when (status) {
                                            OutreachStatus.PENDING -> Color(0xFFFFD700)
                                            OutreachStatus.COMPLETED -> Color(0xFF10B981)
                                            OutreachStatus.SCHEDULED -> Color(0xFF3B82F6)
                                            OutreachStatus.CANCELLED -> Color(0xFFEF4444)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }

                // Weekly Trend
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Weekly Trend",
                            style = MaterialTheme.typography.h6
                        )
                        weeklyAnalytics?.weeklyTrend?.let { trend ->
                            LineChart(
                                entries = trend.map { analytics ->
                                    LineEntry(
                                        x = analytics.date.dayOfWeek.value.toFloat(),
                                        y = analytics.outreachCount.toFloat(),
                                        label = analytics.date.dayOfWeek.name
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DateSelector(onDateSelected: (LocalDate) -> Unit) {
        var selectedDate by remember { mutableStateOf(LocalDate.now()) }
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                selectedDate = selectedDate.minusDays(1)
                onDateSelected(selectedDate)
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day")
            }

            Text(
                text = selectedDate.format(formatter),
                style = MaterialTheme.typography.h5
            )

            IconButton(onClick = {
                selectedDate = selectedDate.plusDays(1)
                onDateSelected(selectedDate)
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
            }
        }
    }

    @Composable
    fun MetricCard(title: String, value: String) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.caption
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}
