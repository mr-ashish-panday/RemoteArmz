package com.remotearmz.commandcenter.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remotearmz.commandcenter.data.model.BackupFile
import com.remotearmz.commandcenter.data.model.BackupStatus
import com.remotearmz.commandcenter.data.repository.GoogleDriveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleDriveViewModel @Inject constructor(
    private val googleDriveRepository: GoogleDriveRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<GoogleDriveUiState>(GoogleDriveUiState.Initial)
    val uiState: StateFlow<GoogleDriveUiState> = _uiState

    fun connectToGoogleDrive(email: String) {
        viewModelScope.launch {
            try {
                val config = googleDriveRepository.authenticate()
                if (config != null) {
                    _uiState.value = GoogleDriveUiState.Connected(config)
                } else {
                    _uiState.value = GoogleDriveUiState.Error("Failed to connect to Google Drive")
                }
            } catch (e: Exception) {
                _uiState.value = GoogleDriveUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createBackup(data: ByteArray, fileName: String) {
        viewModelScope.launch {
            try {
                val backupFile = googleDriveRepository.createBackupFile(data, fileName)
                _uiState.value = GoogleDriveUiState.BackupCreated(backupFile)
            } catch (e: Exception) {
                _uiState.value = GoogleDriveUiState.Error(e.message ?: "Failed to create backup")
            }
        }
    }

    fun restoreBackup(backupId: String) {
        viewModelScope.launch {
            try {
                val data = googleDriveRepository.restoreBackupFile(backupId)
                if (data != null) {
                    _uiState.value = GoogleDriveUiState.BackupRestored(backupId)
                } else {
                    _uiState.value = GoogleDriveUiState.Error("Failed to restore backup")
                }
            } catch (e: Exception) {
                _uiState.value = GoogleDriveUiState.Error(e.message ?: "Failed to restore backup")
            }
        }
    }

    fun deleteBackup(backupId: String) {
        viewModelScope.launch {
            try {
                val success = googleDriveRepository.deleteBackup(backupId)
                if (success) {
                    _uiState.value = GoogleDriveUiState.BackupDeleted(backupId)
                } else {
                    _uiState.value = GoogleDriveUiState.Error("Failed to delete backup")
                }
            } catch (e: Exception) {
                _uiState.value = GoogleDriveUiState.Error(e.message ?: "Failed to delete backup")
            }
        }
    }

    fun listBackups() {
        viewModelScope.launch {
            try {
                val backups = googleDriveRepository.listBackups()
                _uiState.value = GoogleDriveUiState.BackupsLoaded(backups)
            } catch (e: Exception) {
                _uiState.value = GoogleDriveUiState.Error(e.message ?: "Failed to load backups")
            }
        }
    }
}

sealed class GoogleDriveUiState {
    object Initial : GoogleDriveUiState()
    data class Connected(val config: GoogleDriveConfig) : GoogleDriveUiState()
    data class BackupCreated(val backup: BackupFile) : GoogleDriveUiState()
    data class BackupRestored(val backupId: String) : GoogleDriveUiState()
    data class BackupDeleted(val backupId: String) : GoogleDriveUiState()
    data class BackupsLoaded(val backups: List<BackupFile>) : GoogleDriveUiState()
    data class Error(val message: String) : GoogleDriveUiState()
}
