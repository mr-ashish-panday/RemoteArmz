package com.remotearmz.commandcenter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.remotearmz.commandcenter.data.model.OutreachType
import com.remotearmz.commandcenter.data.model.OutreachStatus

@Entity(tableName = "outreach")
data class OutreachEntity(
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
