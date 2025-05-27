package com.remotearmz.commandcenter.data.repository

import com.remotearmz.commandcenter.data.model.Target
import com.remotearmz.commandcenter.data.model.TargetCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TargetRepository {
    // Target operations
    suspend fun addTarget(target: Target): Target
    suspend fun updateTarget(target: Target): Target
    suspend fun deleteTarget(targetId: String)
    fun getTargets(): Flow<List<Target>>
    fun getTargetsByType(targetType: TargetType): Flow<List<Target>>
    fun getTargetsByStatus(status: TargetStatus): Flow<List<Target>>
    fun getTargetsByCategory(category: String): Flow<List<Target>>
    fun getTargetById(targetId: String): Flow<Target?>
    fun getOverdueTargets(): Flow<List<Target>>
    fun getInProgressTargets(): Flow<List<Target>>
    fun getCompletedTargets(): Flow<List<Target>>
    
    // Target progress operations
    suspend fun addProgress(targetId: String, progress: Double, notes: String? = null)
    fun getTargetProgress(targetId: String): Flow<List<TargetProgress>>
    
    // Category operations
    suspend fun addCategory(category: TargetCategory): TargetCategory
    suspend fun updateCategory(category: TargetCategory): TargetCategory
    suspend fun deleteCategory(categoryId: String)
    fun getCategories(): Flow<List<TargetCategory>>
    fun getCategoryById(categoryId: String): Flow<TargetCategory?>
}

class DefaultTargetRepository @Inject constructor(
    private val targetDao: TargetDao,
    private val categoryDao: CategoryDao
) : TargetRepository {
    override suspend fun addTarget(target: Target): Target {
        val id = targetDao.insertTarget(target)
        return target.copy(id = id.toString())
    }

    override suspend fun updateTarget(target: Target): Target {
        targetDao.updateTarget(target)
        return target
    }

    override suspend fun deleteTarget(targetId: String) {
        targetDao.deleteTarget(targetId)
    }

    override fun getTargets(): Flow<List<Target>> = targetDao.getAllTargets()

    override fun getTargetsByType(targetType: TargetType): Flow<List<Target>> =
        targetDao.getTargetsByType(targetType)

    override fun getTargetsByStatus(status: TargetStatus): Flow<List<Target>> =
        targetDao.getTargetsByStatus(status)

    override fun getTargetsByCategory(category: String): Flow<List<Target>> =
        targetDao.getTargetsByCategory(category)

    override fun getTargetById(targetId: String): Flow<Target?> =
        targetDao.getTargetById(targetId)

    override fun getOverdueTargets(): Flow<List<Target>> =
        targetDao.getOverdueTargets()

    override fun getInProgressTargets(): Flow<List<Target>> =
        targetDao.getInProgressTargets()

    override fun getCompletedTargets(): Flow<List<Target>> =
        targetDao.getCompletedTargets()

    override suspend fun addProgress(targetId: String, progress: Double, notes: String?) {
        val progressRecord = TargetProgress(
            date = LocalDate.now(),
            progress = progress,
            notes = notes
        )
        targetDao.addProgress(targetId, progressRecord)
        targetDao.updateTargetProgress(targetId, progress)
    }

    override fun getTargetProgress(targetId: String): Flow<List<TargetProgress>> =
        targetDao.getTargetProgress(targetId)

    override suspend fun addCategory(category: TargetCategory): TargetCategory {
        val id = categoryDao.insertCategory(category)
        return category.copy(id = id.toString())
    }

    override suspend fun updateCategory(category: TargetCategory): TargetCategory {
        categoryDao.updateCategory(category)
        return category
    }

    override suspend fun deleteCategory(categoryId: String) {
        categoryDao.deleteCategory(categoryId)
    }

    override fun getCategories(): Flow<List<TargetCategory>> =
        categoryDao.getAllCategories()

    override fun getCategoryById(categoryId: String): Flow<TargetCategory?> =
        categoryDao.getCategoryById(categoryId)
}
