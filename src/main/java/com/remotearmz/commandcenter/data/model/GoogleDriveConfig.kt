package com.remotearmz.commandcenter.data.model

import java.time.Instant

data class GoogleDriveConfig(
    val accountId: String,
    val refreshToken: String,
    val accessToken: String,
    val tokenExpiration: Instant,
    val backupFolderId: String? = null,
    val lastBackupTime: Instant? = null,
    val backupFrequency: BackupFrequency = BackupFrequency.DAILY,
    val isEnabled: Boolean = true,
    val maxBackupVersions: Int = 5
)

enum class BackupFrequency {
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY
}
