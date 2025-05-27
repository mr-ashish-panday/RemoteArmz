package com.remotearmz.commandcenter.ui.campaign

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remotearmz.commandcenter.data.model.Campaign
import com.remotearmz.commandcenter.data.model.CampaignTemplate
import com.remotearmz.commandcenter.data.model.CampaignSegment
import com.remotearmz.commandcenter.ui.campaign.CampaignViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignDetailsScreen(
    campaignId: String,
    viewModel: CampaignViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val selectedCampaign by viewModel.selectedCampaign.collectAsState()

    LaunchedEffect(campaignId) {
        viewModel.getCampaign(campaignId)
    }

    selectedCampaign?.let { campaign ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(campaign.name) },
                    navigationIcon = {
                        IconButton(onClick = { /* TODO: Navigate back */ }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Add share */ }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Campaign Overview
                CampaignOverview(campaign)

                // Tabs
                Tabs(
                    selectedTabIndex = 0,
                    onTabSelected = { /* TODO: Handle tab selection */ }
                ) {
                    Tab(text = { Text("Templates") })
                    Tab(text = { Text("Segments") })
                    Tab(text = { Text("Metrics") })
                    Tab(text = { Text("Analytics") })
                }

                // Content based on selected tab
                when (0) { // Replace with actual selected tab index
                    0 -> TemplatesTab(campaign.templates)
                    1 -> SegmentsTab(campaign.segments)
                    2 -> MetricsTab(campaign)
                    3 -> AnalyticsTab(campaign)
                }
            }
        }
    }
}

@Composable
fun CampaignOverview(campaign: Campaign) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Campaign Status
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    campaign.name,
                    style = MaterialTheme.typography.titleLarge
                )
                StatusBadge(campaign.status)
            }

            // Campaign Details
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    campaign.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Type: ${campaign.type}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Priority: ${campaign.priority}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Campaign Timeline
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Start: ${campaign.startDate}",
                    style = MaterialTheme.typography.bodySmall
                )
                campaign.endDate?.let { endDate ->
                    Text(
                        "End: $endDate",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun TemplatesTab(templates: List<CampaignTemplate>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(templates) { template ->
            TemplateCard(template)
        }
    }
}

@Composable
fun TemplateCard(template: CampaignTemplate) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                template.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                template.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Text(
                "Order: ${template.order}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Delay: ${template.delay ?: 0} minutes",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SegmentsTab(segments: List<CampaignSegment>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(segments) { segment ->
            SegmentCard(segment)
        }
    }
}

@Composable
fun SegmentCard(segment: CampaignSegment) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                segment.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Size: ${segment.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Templates: ${segment.templateIds.size}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MetricsTab(campaign: Campaign) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MetricRow(
            label = "Sent",
            value = campaign.sentCount.toString(),
            icon = Icons.Default.Email
        )
        MetricRow(
            label = "Responses",
            value = campaign.responseCount.toString(),
            icon = Icons.Default.ThumbUp
        )
        MetricRow(
            label = "Conversions",
            value = campaign.conversionCount.toString(),
            icon = Icons.Default.ShoppingCart
        )
        MetricRow(
            label = "Open Rate",
            value = "${(campaign.openRate * 100).toInt()}%",
            icon = Icons.Default.Visibility
        )
        MetricRow(
            label = "Click Rate",
            value = "${(campaign.clickRate * 100).toInt()}%",
            icon = Icons.Default.OpenInNew
        )
        MetricRow(
            label = "Response Rate",
            value = "${(campaign.responseRate * 100).toInt()}%",
            icon = Icons.Default.Message
        )
        MetricRow(
            label = "Conversion Rate",
            value = "${(campaign.conversionRate * 100).toInt()}%",
            icon = Icons.Default.ShoppingCart
        )
    }
}

@Composable
fun MetricRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AnalyticsTab(campaign: Campaign) {
    // TODO: Implement analytics visualization
    Text("Analytics coming soon...")
}
