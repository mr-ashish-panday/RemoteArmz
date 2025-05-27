package com.remotearmz.commandcenter.data.dao

import androidx.room.*
import com.remotearmz.commandcenter.data.entity.OutreachEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OutreachDao {
    @Query("SELECT * FROM outreach WHERE clientId = :clientId ORDER BY outreachDate DESC")
    fun getOutreachByClient(clientId: String): Flow<List<OutreachEntity>>

    @Query("SELECT * FROM outreach WHERE leadId = :leadId ORDER BY outreachDate DESC")
    fun getOutreachByLead(leadId: String): Flow<List<OutreachEntity>>

    @Query("SELECT * FROM outreach WHERE outreachDate BETWEEN :startDate AND :endDate ORDER BY outreachDate DESC")
    fun getOutreachByDateRange(startDate: Long, endDate: Long): Flow<List<OutreachEntity>>

    @Query("SELECT COUNT(*) FROM outreach WHERE outreachDate BETWEEN :startDate AND :endDate")
    fun getOutreachCountByDateRange(startDate: Long, endDate: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM outreach WHERE outreachDate BETWEEN :startDate AND :endDate AND status = :status")
    fun getOutreachCountByStatus(startDate: Long, endDate: Long, status: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutreach(outreach: OutreachEntity)

    @Update
    suspend fun updateOutreach(outreach: OutreachEntity)

    @Delete
    suspend fun deleteOutreach(outreach: OutreachEntity)

    @Query("SELECT COUNT(*) FROM outreach WHERE outreachDate BETWEEN :startDate AND :endDate AND outreachType = :type")
    suspend fun getOutreachCountByType(startDate: Long, endDate: Long, type: String): Int
}
