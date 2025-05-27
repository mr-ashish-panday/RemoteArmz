package com.remotearmz.commandcenter.ui.backup

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
import com.remotearmz.commandcenter.data.model.BackupFile
import com.remotearmz.commandcenter.data.model.BackupStatus
import com.remotearmz.commandcenter.ui.backup.BackupViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleDriveBackupScreen(
    viewModel: BackupViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val uiState by viewModel.uiState.collectAsState()
    var isSignedIn by remember { mutableStateOf(false) }
    var googleSignInClient: GoogleSignInClient? = null

    // Google Sign-In Configuration
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        task.addOnSuccessListener { account ->
            isSignedIn = true
            viewModel.connectToGoogleDrive(account.email)
        }
    }

    LaunchedEffect(Unit) {
        googleSignInClient = GoogleSignIn.getClient(
            LocalContext.current,
            gso
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Google Drive Backup") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isSignedIn) {
                Button(
                    onClick = {
                        launcher.launch(googleSignInClient?.signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Google, "Google")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign in with Google")
                }
            } else {
                Text(
                    "Connected to Google Drive",
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Backup Controls
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.createBackup()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Backup, "Backup")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Backup")
                }

                // Backup List
                if (uiState.backups.isNotEmpty()) {
                    Text(
                        "Backups",
                        style = MaterialTheme.typography.titleMedium
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.backups) { backup ->
                            BackupItem(
                                backup = backup,
                                onRestoreClick = {
                                    scope.launch {
                                        viewModel.restoreBackup(backup.id)
                                    }
                                },
                                onDeleteClick = {
                                    scope.launch {
                                        viewModel.deleteBackup(backup.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackupItem(
    backup: BackupFile,
    onRestoreClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(backup.name)
                Text(
                    "Size: ${backup.size / 1024 / 1024} MB",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Created: ${backup.createdAt}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Row {
                IconButton(onClick = onRestoreClick) {
                    Icon(Icons.Default.Restore, "Restore")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, "Delete")
                }
            }
        }
    }
}
