package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChecklistReportEntity
import com.example.data.model.FindingEntity
import com.example.ui.theme.*
import com.example.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SecurityViewModel,
    onNavigateToChecklists: () -> Unit,
    onNavigateToFindings: () -> Unit,
    onNavigateToLaws: () -> Unit
) {
    val reports by viewModel.allReports.collectAsState()
    val findings by viewModel.allFindings.collectAsState()

    // Calculations
    val totalAudits = reports.size
    val avgCompliance = if (reports.isNotEmpty()) {
        reports.map { it.scorePercentage }.average().toFloat()
    } else {
        0f
    }

    val pendingFindings = findings.count { it.status == "PENDIENTE" }
    val criticalFindings = findings.count { it.riskLevel == "CRITICO" && it.status != "SOLUCIONADO" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Shield,
                            contentDescription = "Shield Icon",
                            tint = SafetyOrange,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "SST Argentina",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            // Welcome Header Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SafetyBlue, Color(0xFF0D47A1))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Sistema Integrado de SST",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Higiene y Seguridad en el Trabajo",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Herramienta de control legal para la República Argentina (Ley 19.587 / Dec. 351 / Dec. 911).",
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Key Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Compliance Stat
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_compliance"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Cumplimiento",
                                tint = SafetyGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Cumplimiento",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                String.format(Locale.getDefault(), "%.1f%%", avgCompliance),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = if (avgCompliance >= 80f) SafetyGreen else if (avgCompliance >= 50f) SafetyYellow else SafetyRed
                            )
                            Text(
                                "$totalAudits auditorías",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Alert/Findings Stat
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_findings"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = "Hallazgos Activos",
                                tint = if (criticalFindings > 0) SafetyRed else SafetyOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Hallazgos Activos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "$pendingFindings",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = if (criticalFindings > 0) SafetyRed else SafetyOrange
                            )
                            Text(
                                "$criticalFindings Críticos",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (criticalFindings > 0) SafetyRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontWeight = if (criticalFindings > 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Quick Operations Row
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Operaciones Rápidas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToChecklists,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("btn_quick_audit"),
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrange),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "Nueva Auditoria")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Checklist", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onNavigateToFindings,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("btn_quick_report"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AddAlarm, contentDescription = "Reportar Hallazgo")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reportar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Central Laws Quick Link Button
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLaws() }
                        .testTag("card_laws_link"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Gavel,
                                contentDescription = "Leyes",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Biblioteca de Leyes y Decretos",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Leyes 19587, 24557 y decretos reglamentarios argentinos.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            Icons.Default.ArrowForwardIos,
                            contentDescription = "Ver Leyes",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Recent Audits Preview
            item {
                Text(
                    "Auditorías Recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (reports.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = "Sin registros",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                "No hay auditorías registradas todavía.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Utilice la pestaña 'Checklist' para registrar su primera inspección.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            } else {
                items(reports.take(3)) { report ->
                    RecentReportItem(report = report) {
                        // Switch onto lists tab then trigger detail selection
                        viewModel.selectReport(report.id)
                        onNavigateToChecklists()
                    }
                }
            }
        }
    }
}

@Composable
fun RecentReportItem(report: ChecklistReportEntity, onClick: () -> Unit) {
    val dateStr = remember(report.dateMillis) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(report.dateMillis))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("recent_report_item_${report.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle with Score percentage
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            report.scorePercentage >= 85f -> SafetyGreen.copy(alpha = 0.15f)
                            report.scorePercentage >= 65f -> SafetyYellow.copy(alpha = 0.2f)
                            else -> SafetyRed.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    String.format(Locale.getDefault(), "%.0f%%", report.scorePercentage),
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        report.scorePercentage >= 85f -> SafetyGreen
                        report.scorePercentage >= 65f -> Color(0xFFC79100) // Darker yellow for text legibility
                        else -> SafetyRed
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    report.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Auditor: ${report.auditorName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Detalle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
