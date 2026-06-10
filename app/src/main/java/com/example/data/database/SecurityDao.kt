package com.example.data.database

import androidx.room.*
import com.example.data.model.ChecklistAnswerEntity
import com.example.data.model.ChecklistReportEntity
import com.example.data.model.FindingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {

    // --- Checklist Reports ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ChecklistReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<ChecklistAnswerEntity>)

    @Query("SELECT * FROM checklist_reports ORDER BY dateMillis DESC")
    fun getAllReports(): Flow<List<ChecklistReportEntity>>

    @Query("SELECT * FROM checklist_answers WHERE reportId = :reportId")
    fun getAnswersForReport(reportId: Int): Flow<List<ChecklistAnswerEntity>>

    @Query("DELETE FROM checklist_reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: Int)

    @Query("DELETE FROM checklist_answers WHERE reportId = :reportId")
    suspend fun deleteAnswersForReport(reportId: Int)

    @Transaction
    suspend fun deleteFullReport(reportId: Int) {
        deleteReport(reportId)
        deleteAnswersForReport(reportId)
    }

    // --- Findings (Hallazgos) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinding(finding: FindingEntity): Long

    @Query("SELECT * FROM findings ORDER BY dateMillis DESC")
    fun getAllFindings(): Flow<List<FindingEntity>>

    @Query("UPDATE findings SET status = :status WHERE id = :id")
    suspend fun updateFindingStatus(id: Int, status: String)

    @Query("DELETE FROM findings WHERE id = :id")
    suspend fun deleteFinding(id: Int)
}
