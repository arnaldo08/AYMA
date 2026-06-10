package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "findings")
data class FindingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val location: String,
    val riskLevel: String, // "BAJO", "MEDIO", "ALTO", "CRITICO"
    val regulatoryReference: String, // Referencia de ley/decreto
    val actionRequired: String, // Acción correctiva
    val status: String, // "PENDIENTE", "EN_PROCESO", "SOLUCIONADO"
    val dateMillis: Long,
    val inspectorName: String
)
