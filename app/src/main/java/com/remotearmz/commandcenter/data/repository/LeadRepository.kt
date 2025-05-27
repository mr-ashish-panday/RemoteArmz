package com.remotearmz.commandcenter.data.repository

import com.remotearmz.commandcenter.data.model.Lead
import kotlinx.coroutines.flow.Flow

interface LeadRepository {
    fun getLeads(): Flow<List<Lead>>
    suspend fun addLead(lead: Lead)
    suspend fun updateLead(lead: Lead)
    suspend fun deleteLead(lead: Lead)
    fun getLeadsByStatus(status: LeadStatus): Flow<List<Lead>>
}

class DefaultLeadRepository : LeadRepository {
    private val leads = mutableListOf(
        Lead(
            id = "1",
            name = "John Doe",
            email = "john@example.com",
            phone = "+1234567890",
            company = "TechCorp",
            designation = "CTO",
            source = LeadSource.WEBSITE,
            status = LeadStatus.NEW,
            notes = "Interested in social media management",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ),
        Lead(
            id = "2",
            name = "Jane Smith",
            email = "jane@example.com",
            phone = "+0987654321",
            company = "StartupXYZ",
            designation = "Marketing Manager",
            source = LeadSource.REFERRAL,
            status = LeadStatus.CONTACTED,
            notes = "Followed up on LinkedIn",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    )

    override fun getLeads(): Flow<List<Lead>> = flow { emit(leads) }

    override suspend fun addLead(lead: Lead) {
        leads.add(lead)
    }

    override suspend fun updateLead(lead: Lead) {
        val index = leads.indexOfFirst { it.id == lead.id }
        if (index != -1) {
            leads[index] = lead
        }
    }

    override suspend fun deleteLead(lead: Lead) {
        leads.remove(lead)
    }

    override fun getLeadsByStatus(status: LeadStatus): Flow<List<Lead>> = flow {
        emit(leads.filter { it.status == status })
    }
}
