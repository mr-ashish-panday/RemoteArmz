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
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineEntry
import com.remotearmz.commandcenter.R
import com.remotearmz.commandcenter.data.model.OutreachType
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class OutreachPerformanceFragment : Fragment() {
    private val viewModel: OutreachAnalyticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    OutreachPerformance()
                }
            }
        }
    }

    @Composable
    fun OutreachPerformance() {
        val analyticsByType by viewModel.analyticsByType.collectAsState()
        val currentType = remember { mutableStateOf(OutreachType.EMAIL) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Outreach Performance") },
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
                // Type Selector
                TypeSelector(
                    currentType = currentType.value,
                    onTypeSelected = { type ->
                        currentType.value = type
                        viewModel.getAnalyticsForType(type)
                    }
                )

                // Performance Metrics
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Performance Metrics",
                            style = MaterialTheme.typography.h6
                        )
                        analyticsByType?.get(currentType.value)?.let { analytics ->
                            MetricGrid(analytics)
                        }
                    }
                }

                // Response Time Analysis
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Response Time Analysis",
                            style = MaterialTheme.typography.h6
                        )
                        analyticsByType?.get(currentType.value)?.let { analytics ->
                            LineChart(
                                entries = analytics.map { outreach ->
                                    LineEntry(
                                        x = outreach.date.dayOfYear.toFloat(),
                                        y = outreach.averageResponseTime.toFloat() / 1000 / 60,
                                        label = outreach.date.format(DateTimeFormatter.ofPattern("dd MMM"))
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
                        analyticsByType?.get(currentType.value)?.let { analytics ->
                            BarChart(
                                entries = analytics.flatMap { outreach ->
                                    outreach.outreachByStatus.entries.map { (status, count) ->
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
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TypeSelector(
        currentType: OutreachType,
        onTypeSelected: (OutreachType) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutreachType.values().forEach { type ->
                Button(
                    onClick = { onTypeSelected(type) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (type == currentType) Color(0xFF10B981) else MaterialTheme.colors.primary
                    )
                ) {
                    Text(type.name)
                }
            }
        }
    }

    @Composable
    fun MetricGrid(analytics: List<OutreachAnalytics>) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricCard(
                title = "Total Outreach",
                value = analytics.sumOf { it.outreachCount }.toString(),
                color = Color(0xFF10B981)
            )
            MetricCard(
                title = "Conversion Rate",
                value = "${"%.1f".format(analytics.averageOf { it.conversionRate })}%",
                color = Color(0xFF3B82F6)
            )
            MetricCard(
                title = "Avg Response Time",
                value = "${"%.1f".format(analytics.averageOf { it.averageResponseTime / 1000 / 60 })} mins",
                color = Color(0xFFF59E0B)
            )
            MetricCard(
                title = "Success Rate",
                value = "${"%.1f".format(
                    analytics.averageOf { it.successfulOutreach.toDouble() / it.totalOutreach * 100 }
                )}%",
                color = Color(0xFF10B981)
            )
        }
    }

    @Composable
    fun MetricCard(title: String, value: String, color: Color) {
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
                    style = MaterialTheme.typography.h6,
                    color = color
                )
            }
        }
    }
}
