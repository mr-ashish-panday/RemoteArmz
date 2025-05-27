package com.remotearmz.commandcenter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.remotearmz.commandcenter.data.model.ClientStatus

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val whatsapp: String,
    val email: String,
    val instagram: String,
    val monthlyCharge: Double,
    val deliverables: String, // JSON string of List<String>
    val paymentDate: String,
    val status: ClientStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
