package com.remotearmz.commandcenter.ui.campaign

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.remotearmz.commandcenter.data.model.Campaign
import com.remotearmz.commandcenter.data.model.CampaignStatus
import com.remotearmz.commandcenter.data.model.CampaignType
import com.remotearmz.commandcenter.ui.campaign.CampaignViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignScreen(
    viewModel: CampaignViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val selectedCampaign by viewModel.selectedCampaign.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campaign Management") },
                actions = {
                    IconButton(onClick = { /* TODO: Add filter */ }) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                    IconButton(onClick = { /* TODO: Add search */ }) {
                        Icon(Icons.Default.Search, "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        viewModel.createCampaign(
                            Campaign(
                                id = "",
                                name = "New Campaign",
                                description = "",
                                type = CampaignType.EMAIL,
                                status = CampaignStatus.DRAFT,
                                priority = CampaignPriority.MEDIUM,
                                targetCount = 0,
                                sentCount = 0,
                                responseCount = 0,
                                conversionCount = 0,
                                startDate = Instant.now(),
                                endDate = null,
                                budget = null,
                                templates = emptyList(),
                                segments = emptyList(),
                                schedule = null,
                                createdBy = "",
                                createdAt = Instant.now(),
                                updatedAt = Instant.now(),
                                metadata = emptyMap()
                            )
                        )
                    }
                }
            ) {
                Icon(Icons.Default.Add, "Create Campaign")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Campaign List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(campaigns) { campaign ->
                    CampaignCard(
                        campaign = campaign,
                        onEditClick = { viewModel.getCampaign(campaign.id) },
                        onStartClick = { viewModel.startCampaign(campaign.id) },
                        onPauseClick = { viewModel.pauseCampaign(campaign.id) },
                        onResumeClick = { viewModel.resumeCampaign(campaign.id) },
                        onCompleteClick = { viewModel.completeCampaign(campaign.id) },
                        onCancelClick = { viewModel.cancelCampaign(campaign.id) },
                        onDeleteClick = { viewModel.deleteCampaign(campaign.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignCard(
    campaign: Campaign,
    onEditClick: () -> Unit,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEditClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Campaign Header
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    campaign.name,
                    style = MaterialTheme.typography.titleMedium
                )
                StatusBadge(campaign.status)
            }

            // Campaign Details
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    campaign.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
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

            // Campaign Metrics
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricBadge(
                    icon = Icons.Default.Email,
                    label = "Sent",
                    value = campaign.sentCount.toString()
                )
                MetricBadge(
                    icon = Icons.Default.ThumbUp,
                    label = "Responses",
                    value = campaign.responseCount.toString()
                )
                MetricBadge(
                    icon = Icons.Default.ShoppingCart,
                    label = "Conversions",
                    value = campaign.conversionCount.toString()
                )
            }

            // Campaign Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (campaign.status) {
                    CampaignStatus.DRAFT -> {
                        TextButton(onClick = onStartClick) {
                            Icon(Icons.Default.PlayArrow, "Start")
                            Text("Start")
                        }
                        TextButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, "Delete")
                            Text("Delete")
                        }
                    }
                    CampaignStatus.ACTIVE -> {
                        TextButton(onClick = onPauseClick) {
                            Icon(Icons.Default.Pause, "Pause")
                            Text("Pause")
                        }
                        TextButton(onClick = onCompleteClick) {
                            Icon(Icons.Default.Done, "Complete")
                            Text("Complete")
                        }
                    }
                    CampaignStatus.PAUSED -> {
                        TextButton(onClick = onResumeClick) {
                            Icon(Icons.Default.PlayArrow, "Resume")
                            Text("Resume")
                        }
                        TextButton(onClick = onCompleteClick) {
                            Icon(Icons.Default.Done, "Complete")
                            Text("Complete")
                        }
                    }
                    CampaignStatus.COMPLETED -> {
                        TextButton(onClick = onCancelClick) {
                            Icon(Icons.Default.Cancel, "Cancel")
                            Text("Cancel")
                        }
                    }
                    CampaignStatus.ARCHIVED -> {
                        TextButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, "Delete")
                            Text("Delete")
                        }
                    }
                    CampaignStatus.CANCELLED -> {
                        TextButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, "Delete")
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: CampaignStatus) {
    Card(
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                CampaignStatus.ACTIVE -> MaterialTheme.colorScheme.primary
                CampaignStatus.PAUSED -> MaterialTheme.colorScheme.secondary
                CampaignStatus.COMPLETED -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Text(
            status.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun MetricBadge(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
