package com.remotearmz.commandcenter.data.repository

import com.remotearmz.commandcenter.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface IntegrationRepository {
    // Integration Management
    suspend fun addIntegration(integration: Integration): Integration
    suspend fun updateIntegration(integration: Integration): Integration
    suspend fun deleteIntegration(integrationId: String)
    fun getIntegrations(): Flow<List<Integration>>
    fun getIntegrationById(integrationId: String): Flow<Integration?>
    fun getIntegrationsByType(type: IntegrationType): Flow<List<Integration>>
    fun getIntegrationsByProvider(provider: IntegrationProvider): Flow<List<Integration>>
    fun getConnectedIntegrations(): Flow<List<Integration>>
    fun getDisconnectedIntegrations(): Flow<List<Integration>>

    // Authentication
    suspend fun authenticateIntegration(
        provider: IntegrationProvider,
        authCode: String
    ): IntegrationToken?
    
    suspend fun refreshToken(integrationId: String): IntegrationToken?
    
    suspend fun revokeToken(integrationId: String)

    // Sync Management
    suspend fun startSync(integrationId: String, syncType: String)
    fun getSyncStatus(integrationId: String): Flow<IntegrationSync?>
    suspend fun updateSyncStatus(
        integrationId: String,
        status: IntegrationStatus,
        error: String? = null
    )
    suspend fun scheduleNextSync(integrationId: String, nextSync: Long)

    // Configuration
    fun getIntegrationConfig(provider: IntegrationProvider): IntegrationConfig?
    suspend fun saveIntegrationConfig(
        provider: IntegrationProvider,
        config: IntegrationConfig
    )
    suspend fun updateIntegrationConfig(
        provider: IntegrationProvider,
        config: IntegrationConfig
    )
    
    // Error Handling
    suspend fun logIntegrationError(
        integrationId: String,
        error: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    fun getIntegrationErrors(integrationId: String): Flow<List<String>>
    
    // Status Updates
    suspend fun updateIntegrationStatus(
        integrationId: String,
        status: IntegrationStatus,
        error: String? = null
    )
}

class DefaultIntegrationRepository @Inject constructor(
    private val integrationDao: IntegrationDao,
    private val authManager: AuthManager,
    private val syncManager: SyncManager
) : IntegrationRepository {
    override suspend fun addIntegration(integration: Integration): Integration {
        val id = integrationDao.insertIntegration(integration)
        return integration.copy(id = id.toString())
    }

    override suspend fun updateIntegration(integration: Integration): Integration {
        integrationDao.updateIntegration(integration)
        return integration
    }

    override suspend fun deleteIntegration(integrationId: String) {
        integrationDao.deleteIntegration(integrationId)
    }

    override fun getIntegrations(): Flow<List<Integration>> =
        integrationDao.getAllIntegrations()

    override fun getIntegrationById(integrationId: String): Flow<Integration?> =
        integrationDao.getIntegrationById(integrationId)

    override fun getIntegrationsByType(type: IntegrationType): Flow<List<Integration>> =
        integrationDao.getIntegrationsByType(type)

    override fun getIntegrationsByProvider(provider: IntegrationProvider): Flow<List<Integration>> =
        integrationDao.getIntegrationsByProvider(provider)

    override fun getConnectedIntegrations(): Flow<List<Integration>> =
        integrationDao.getConnectedIntegrations()

    override fun getDisconnectedIntegrations(): Flow<List<Integration>> =
        integrationDao.getDisconnectedIntegrations()

    override suspend fun authenticateIntegration(
        provider: IntegrationProvider,
        authCode: String
    ): IntegrationToken? {
        return authManager.authenticate(provider, authCode)
    }

    override suspend fun refreshToken(integrationId: String): IntegrationToken? {
        val integration = integrationDao.getIntegrationByIdBlocking(integrationId) ?: return null
        return authManager.refreshToken(integration)
    }

    override suspend fun revokeToken(integrationId: String) {
        val integration = integrationDao.getIntegrationByIdBlocking(integrationId)
        integration?.let { authManager.revokeToken(it) }
    }

    override suspend fun startSync(integrationId: String, syncType: String) {
        syncManager.startSync(integrationId, syncType)
    }

    override fun getSyncStatus(integrationId: String): Flow<IntegrationSync?> =
        syncManager.getSyncStatus(integrationId)

    override suspend fun updateSyncStatus(
        integrationId: String,
        status: IntegrationStatus,
        error: String?
    ) {
        syncManager.updateSyncStatus(integrationId, status, error)
    }

    override suspend fun scheduleNextSync(integrationId: String, nextSync: Long) {
        syncManager.scheduleNextSync(integrationId, nextSync)
    }

    override fun getIntegrationConfig(provider: IntegrationProvider): IntegrationConfig? {
        return integrationDao.getIntegrationConfig(provider)
    }

    override suspend fun saveIntegrationConfig(
        provider: IntegrationProvider,
        config: IntegrationConfig
    ) {
        integrationDao.saveIntegrationConfig(provider, config)
    }

    override suspend fun updateIntegrationConfig(
        provider: IntegrationProvider,
        config: IntegrationConfig
    ) {
        integrationDao.updateIntegrationConfig(provider, config)
    }

    override suspend fun logIntegrationError(
        integrationId: String,
        error: String,
        timestamp: Long
    ) {
        integrationDao.logIntegrationError(integrationId, error, timestamp)
    }

    override fun getIntegrationErrors(integrationId: String): Flow<List<String>> =
        integrationDao.getIntegrationErrors(integrationId)

    override suspend fun updateIntegrationStatus(
        integrationId: String,
        status: IntegrationStatus,
        error: String?
    ) {
        integrationDao.updateIntegrationStatus(integrationId, status, error)
    }
}
