package com.example.data.repository

import com.example.data.database.SecurityDao
import com.example.data.model.ChecklistAnswerEntity
import com.example.data.model.ChecklistReportEntity
import com.example.data.model.FindingEntity
import kotlinx.coroutines.flow.Flow

class SecurityRepository(private val securityDao: SecurityDao) {

    val allReports: Flow<List<ChecklistReportEntity>> = securityDao.getAllReports()
    val allFindings: Flow<List<FindingEntity>> = securityDao.getAllFindings()

    suspend fun saveChecklistReport(
        report: ChecklistReportEntity,
        answers: List<ChecklistAnswerEntity>
    ) {
        // First insert report to get generated ID
        val reportId = securityDao.insertReport(report)
        // Map answers with the correct parent report ID
        val updatedAnswers = answers.map { it.copy(reportId = reportId.toInt()) }
        // Insert answers
        securityDao.insertAnswers(updatedAnswers)
    }

    fun getAnswersForReport(reportId: Int): Flow<List<ChecklistAnswerEntity>> {
        return securityDao.getAnswersForReport(reportId)
    }

    suspend fun deleteFullReport(reportId: Int) {
        securityDao.deleteFullReport(reportId)
    }

    suspend fun saveFinding(finding: FindingEntity) {
        securityDao.insertFinding(finding)
    }

    suspend fun updateFindingStatus(id: Int, status: String) {
        securityDao.updateFindingStatus(id, status)
    }

    suspend fun deleteFinding(id: Int) {
        securityDao.deleteFinding(id)
    }
}
