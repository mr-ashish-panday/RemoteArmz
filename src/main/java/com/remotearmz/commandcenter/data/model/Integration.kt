package com.remotearmz.commandcenter.data.model

enum class IntegrationType {
    EMAIL,
    CRM,
    CALENDAR,
    SOCIAL_MEDIA,
    ANALYTICS,
    STORAGE,
    CUSTOM
}

enum class IntegrationStatus {
    CONNECTED,
    DISCONNECTED,
    ERROR,
    PENDING
}

enum class IntegrationProvider {
    GMAIL,
    OUTLOOK,
    SALESFORCE,
    HUBSPOT,
    GOOGLE_CALENDAR,
    OUTLOOK_CALENDAR,
    LINKEDIN,
    TWITTER,
    GOOGLE_ANALYTICS,
    AWS,
    AZURE,
    CUSTOM
}

data class Integration(
    val id: String,
    val name: String,
    val type: IntegrationType,
    val provider: IntegrationProvider,
    val status: IntegrationStatus,
    val isConnected: Boolean = false,
    val lastSync: Long = 0L,
    val config: Map<String, String> = emptyMap(),
    val error: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class IntegrationConfig(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val scopes: List<String>,
    val authUrl: String,
    val tokenUrl: String,
    val apiEndpoint: String
)

data class IntegrationToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String,
    val scope: String
)

data class IntegrationSync(
    val integrationId: String,
    val syncType: String,
    val lastSync: Long,
    val nextSync: Long,
    val status: IntegrationStatus,
    val error: String? = null
)
