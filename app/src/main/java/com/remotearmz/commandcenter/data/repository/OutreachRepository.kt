package com.remotearmz.commandcenter.data.repository

import com.remotearmz.commandcenter.data.model.Outreach
import com.remotearmz.commandcenter.data.model.OutreachType
import com.remotearmz.commandcenter.data.model.OutreachStatus
import kotlinx.coroutines.flow.Flow

interface OutreachRepository {
    fun getOutreachByClient(clientId: String): Flow<List<Outreach>>
    fun getOutreachByLead(leadId: String): Flow<List<Outreach>>
    fun getOutreachByDateRange(startDate: Long, endDate: Long): Flow<List<Outreach>>
    fun getOutreachCountByDateRange(startDate: Long, endDate: Long): Flow<Int>
    fun getOutreachCountByStatus(startDate: Long, endDate: Long, status: OutreachStatus): Flow<Int>
    suspend fun getOutreachCountByType(startDate: Long, endDate: Long, type: OutreachType): Int
    suspend fun addOutreach(outreach: Outreach)
    suspend fun updateOutreach(outreach: Outreach)
    suspend fun deleteOutreach(outreach: Outreach)
}

class DefaultOutreachRepository(
    private val outreachDao: OutreachDao
) : OutreachRepository {
    override fun getOutreachByClient(clientId: String): Flow<List<Outreach>> {
        return outreachDao.getOutreachByClient(clientId)
    }

    override fun getOutreachByLead(leadId: String): Flow<List<Outreach>> {
        return outreachDao.getOutreachByLead(leadId)
    }

    override fun getOutreachByDateRange(startDate: Long, endDate: Long): Flow<List<Outreach>> {
        return outreachDao.getOutreachByDateRange(startDate, endDate)
    }

    override fun getOutreachCountByDateRange(startDate: Long, endDate: Long): Flow<Int> {
        return outreachDao.getOutreachCountByDateRange(startDate, endDate)
    }

    override fun getOutreachCountByStatus(startDate: Long, endDate: Long, status: OutreachStatus): Flow<Int> {
        return outreachDao.getOutreachCountByStatus(startDate, endDate, status.name)
    }

    override suspend fun getOutreachCountByType(startDate: Long, endDate: Long, type: OutreachType): Int {
        return outreachDao.getOutreachCountByType(startDate, endDate, type.name)
    }

    override suspend fun addOutreach(outreach: Outreach) {
        outreachDao.insertOutreach(outreach.toEntity())
    }

    override suspend fun updateOutreach(outreach: Outreach) {
        outreachDao.updateOutreach(outreach.toEntity())
    }

    override suspend fun deleteOutreach(outreach: Outreach) {
        outreachDao.deleteOutreach(outreach.toEntity())
    }
}
