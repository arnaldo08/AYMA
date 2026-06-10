package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_reports")
data class ChecklistReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val auditorName: String,
    val dateMillis: Long,
    val comments: String,
    val scorePercentage: Float,
    val passedCount: Int,
    val failedCount: Int,
    val naCount: Int
)

@Entity(tableName = "checklist_answers")
data class ChecklistAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reportId: Int,
    val questionId: String,
    val questionText: String,
    val category: String,
    val status: String, // "CUMPLE", "NO_CUMPLE", "NO_APLICA"
    val comment: String
)
