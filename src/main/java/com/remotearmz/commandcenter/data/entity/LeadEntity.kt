package com.remotearmz.commandcenter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.remotearmz.commandcenter.data.model.LeadSource
import com.remotearmz.commandcenter.data.model.LeadStatus

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val company: String,
    val designation: String,
    val source: LeadSource,
    val status: LeadStatus,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
