package com.remotearmz.commandcenter.ui.targets

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
import com.remotearmz.commandcenter.R
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TargetFragment : Fragment() {
    private val viewModel: TargetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    TargetScreen()
                }
            }
        }
    }

    @Composable
    fun TargetScreen() {
        val targets by viewModel.targets.collectAsState(initial = emptyList())
        val stats by viewModel.targetStats.collectAsState()
        val overdueTargets by viewModel.overdueTargets.collectAsState(initial = emptyList())
        val inProgressTargets by viewModel.inProgressTargets.collectAsState(initial = emptyList())
        val completedTargets by viewModel.completedTargets.collectAsState(initial = emptyList())

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Target Management") },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Add new target */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Target")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* TODO: Add new target */ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Target")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Target Stats
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TargetStatCard(
                            title = "Total Targets",
                            value = stats.totalTargets.toString(),
                            color = Color(0xFF10B981)
                        )
                        TargetStatCard(
                            title = "Overdue",
                            value = stats.overdueCount.toString(),
                            color = Color(0xFFEF4444)
                        )
                        TargetStatCard(
                            title = "In Progress",
                            value = stats.inProgressCount.toString(),
                            color = Color(0xFF3B82F6)
                        )
                        TargetStatCard(
                            title = "Completed",
                            value = stats.completedCount.toString(),
                            color = Color(0xFF10B981)
                        )
                    }
                }

                // Target Type Tabs
                TabRow(
                    selectedTabIndex = 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = true,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("All") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("Weekly") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("Monthly") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("Annual") }
                    )
                }

                // Target List
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(targets) { target ->
                        TargetCard(target)
                    }
                }
            }
        }
    }

    @Composable
    fun TargetStatCard(
        title: String,
        value: String,
        color: Color
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
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

    @Composable
    fun TargetCard(target: Target) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = target.title,
                        style = MaterialTheme.typography.h6
                    )
                    TargetStatusChip(target.status)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${target.currentProgress.toInt()}% of ${target.targetValue} ${target.unit}",
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        text = target.category,
                        style = MaterialTheme.typography.caption
                    )
                }

                LinearProgressIndicator(
                    progress = target.currentProgress / target.targetValue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun TargetStatusChip(status: TargetStatus) {
        val color = when (status) {
            TargetStatus.PENDING -> Color(0xFFFFD700)
            TargetStatus.IN_PROGRESS -> Color(0xFF3B82F6)
            TargetStatus.COMPLETED -> Color(0xFF10B981)
            TargetStatus.OVERDUE -> Color(0xFFEF4444)
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
}
