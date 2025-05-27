package com.remotearmz.commandcenter.ui.integrations

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
import java.time.Instant

@AndroidEntryPoint
class IntegrationFragment : Fragment() {
    private val viewModel: IntegrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    IntegrationScreen()
                }
            }
        }
    }

    @Composable
    fun IntegrationScreen() {
        val integrations by viewModel.integrations.collectAsState(initial = emptyList())
        val connectedIntegrations by viewModel.connectedIntegrations.collectAsState(initial = emptyList())
        val disconnectedIntegrations by viewModel.disconnectedIntegrations.collectAsState(initial = emptyList())

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Integrations") },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Add new integration */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Integration")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* TODO: Add new integration */ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Integration")
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
                // Integration Stats
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
                        IntegrationStatCard(
                            title = "Total Integrations",
                            value = integrations.size.toString(),
                            color = Color(0xFF10B981)
                        )
                        IntegrationStatCard(
                            title = "Connected",
                            value = connectedIntegrations.size.toString(),
                            color = Color(0xFF3B82F6)
                        )
                        IntegrationStatCard(
                            title = "Disconnected",
                            value = disconnectedIntegrations.size.toString(),
                            color = Color(0xFFEF4444)
                        )
                    }
                }

                // Integration Categories
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
                        text = { Text("Email") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("CRM") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("Calendar") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("Social") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("Analytics") }
                    )
                    Tab(
                        selected = false,
                        onClick = { /* TODO: Handle tab click */ },
                        text = { Text("Storage") }
                    )
                }

                // Integration List
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(integrations) { integration ->
                        IntegrationCard(integration)
                    }
                }
            }
        }
    }

    @Composable
    fun IntegrationStatCard(
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
    fun IntegrationCard(integration: Integration) {
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
                        text = integration.name,
                        style = MaterialTheme.typography.h6
                    )
                    IntegrationStatusChip(integration.status)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${integration.type.name}",
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        text = integration.provider.name,
                        style = MaterialTheme.typography.caption
                    )
                }

                if (integration.isConnected) {
                    Text(
                        text = "Last Sync: ${Instant.ofEpochMilli(integration.lastSync).toString()}",
                        style = MaterialTheme.typography.caption
                    )
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { /* TODO: Handle settings */ }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(
                        onClick = { viewModel.startSync(integration.id, "FULL") }
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync")
                    }
                    IconButton(
                        onClick = { viewModel.revokeToken(integration.id) }
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Disconnect")
                    }
                }
            }
        }
    }

    @Composable
    fun IntegrationStatusChip(status: IntegrationStatus) {
        val color = when (status) {
            IntegrationStatus.CONNECTED -> Color(0xFF10B981)
            IntegrationStatus.DISCONNECTED -> Color(0xFFEF4444)
            IntegrationStatus.ERROR -> Color(0xFFEF4444)
            IntegrationStatus.PENDING -> Color(0xFFFFD700)
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
