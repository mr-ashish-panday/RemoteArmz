package com.remotearmz.commandcenter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outreach")
data class Outreach(
    @PrimaryKey
    val id: String,
    val clientId: String,
    val leadId: String?,
    val outreachType: OutreachType,
    val outreachDate: Long,
    val status: OutreachStatus,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class OutreachType {
    EMAIL,
    PHONE_CALL,
    LINKEDIN_MESSAGE,
    SOCIAL_MEDIA_POST,
    MEETING,
    OTHER
}

enum class OutreachStatus {
    PENDING,
    COMPLETED,
    SCHEDULED,
    CANCELLED
}
