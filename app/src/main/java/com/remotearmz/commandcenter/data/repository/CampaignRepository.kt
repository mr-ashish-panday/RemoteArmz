package com.remotearmz.commandcenter.data.repository

import com.remotearmz.commandcenter.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface CampaignRepository {
    // Campaign Management
    suspend fun createCampaign(campaign: Campaign): Campaign
    suspend fun updateCampaign(id: String, campaign: Campaign): Campaign
    suspend fun deleteCampaign(id: String): Boolean
    suspend fun getCampaign(id: String): Campaign?
    fun getCampaigns(): Flow<List<Campaign>>
    fun getCampaignsByStatus(status: CampaignStatus): Flow<List<Campaign>>
    fun getCampaignsByType(type: CampaignType): Flow<List<Campaign>>

    // Campaign Metrics
    suspend fun getCampaignMetrics(id: String): CampaignMetrics?
    fun getCampaignMetricsStream(id: String): Flow<CampaignMetrics>
    suspend fun updateCampaignMetrics(id: String, metrics: CampaignMetrics): CampaignMetrics

    // Campaign Scheduling
    suspend fun scheduleCampaign(id: String, schedule: CampaignSchedule): Campaign
    suspend fun unscheduleCampaign(id: String): Campaign
    fun getScheduledCampaigns(): Flow<List<Campaign>>

    // Campaign Templates
    suspend fun createTemplate(template: CampaignTemplate): CampaignTemplate
    suspend fun updateTemplate(id: String, template: CampaignTemplate): CampaignTemplate
    suspend fun deleteTemplate(id: String): Boolean
    suspend fun getTemplate(id: String): CampaignTemplate?
    fun getTemplatesByCampaign(campaignId: String): Flow<List<CampaignTemplate>>

    // Campaign Segments
    suspend fun createSegment(segment: CampaignSegment): CampaignSegment
    suspend fun updateSegment(id: String, segment: CampaignSegment): CampaignSegment
    suspend fun deleteSegment(id: String): Boolean
    suspend fun getSegment(id: String): CampaignSegment?
    fun getSegmentsByCampaign(campaignId: String): Flow<List<CampaignSegment>>

    // Campaign Status
    suspend fun startCampaign(id: String): Campaign
    suspend fun pauseCampaign(id: String): Campaign
    suspend fun resumeCampaign(id: String): Campaign
    suspend fun completeCampaign(id: String): Campaign
    suspend fun cancelCampaign(id: String): Campaign
    suspend fun archiveCampaign(id: String): Campaign

    // Campaign Filters
    fun getFilters(): Flow<List<CampaignFilter>>
    fun getConditions(): Flow<List<CampaignCondition>>
    fun getScheduleTypes(): Flow<List<ScheduleType>>
    fun getFrequencyTypes(): Flow<List<ScheduleFrequency>>

    // Campaign Analytics
    suspend fun getCampaignAnalytics(
        startDate: Instant,
        endDate: Instant,
        filters: List<CampaignFilter>
    ): List<CampaignMetrics>

    suspend fun getCampaignPerformance(
        campaignId: String,
        startDate: Instant,
        endDate: Instant
    ): CampaignMetrics

    // Campaign Reports
    suspend fun generateCampaignReport(
        campaignId: String,
        startDate: Instant,
        endDate: Instant
    ): Map<String, Any>

    // Campaign Templates Management
    suspend fun importTemplates(file: ByteArray): List<CampaignTemplate>
    suspend fun exportTemplates(ids: List<String>): ByteArray
    suspend fun validateTemplate(template: CampaignTemplate): List<String>
}
