package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChecklistAnswerEntity
import com.example.data.model.ChecklistReportEntity
import com.example.data.staticdata.ChecklistItemTemplate
import com.example.data.staticdata.ChecklistTemplate
import com.example.data.staticdata.RegulationsData
import com.example.ui.theme.*
import com.example.viewmodel.SecurityViewModel
import com.example.viewmodel.TempAnswer
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistsScreen(
    viewModel: SecurityViewModel,
    onNavigateToFindings: () -> Unit
) {
    val activeAudit by viewModel.activeAudit.collectAsState()
    val reports by viewModel.allReports.collectAsState()
    val selectedReportId by viewModel.selectedReportId.collectAsState()
    val reportAnswers by viewModel.selectedReportAnswers.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Plantillas/Ejecución, 1 = Historial

    // If there is an active audit in session, override standard tabs and show active inspection UI
    if (activeAudit != null) {
        ActiveAuditRunner(
            activeAuditState = activeAudit!!,
            onAuditorChanged = { viewModel.updateAuditorName(it) },
            onCommentsChanged = { viewModel.updateChecklistComments(it) },
            onStatusChanged = { qId, status -> viewModel.updateAnswerStatus(qId, status) },
            onCommentChanged = { qId, comment -> viewModel.updateAnswerComment(qId, comment) },
            onCancel = { viewModel.cancelActiveAudit() },
            onSubmit = {
                // Determine failed items to create findings automatically!
                val auditState = activeAudit!!
                val auditor = auditState.auditorName.ifEmpty { "Auditor Técnico" }
                val title = auditState.template?.title ?: "Inspección General"
                
                // Track unresolved non-compliances to log as findings
                auditState.answers.forEach { (qId, answer) ->
                    if (answer.status == "NO_CUMPLE") {
                        val findingTitle = "Incumplimiento: ${answer.questionText.take(30)}..."
                        val findingDesc = "Identificado durante la inspección técnica '$title'. " +
                                "La condición no verifica el estándar recomendado: ${answer.questionText}. " +
                                "Comentario observado: ${answer.comment.ifEmpty { "Sin comentarios adicionales." }}"
                        
                        viewModel.logFindingFromChecklist(
                            title = findingTitle,
                            description = findingDesc,
                            location = "Sector inspección '$title'",
                            lawRef = answer.lawReference,
                            inspector = auditor
                        )
                    }
                }

                viewModel.submitActiveAudit {
                    activeTab = 1 // Navigate directly to history on completion!
                }
            }
        )
    } else {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text("Listas de Inspección", fontWeight = FontWeight.Bold) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        )
                    )
                    // Sub Tabs Bar
                    TabRow(selectedTabIndex = activeTab) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = {
                                viewModel.selectReport(null) // Clear historical detail
                                activeTab = 0
                            },
                            text = { Text("Plantillas") },
                            icon = { Icon(Icons.Default.ContentPaste, contentDescription = "Plantillas") },
                            modifier = Modifier.testTag("tab_templates")
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = { Text("Historial") },
                            icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
                            modifier = Modifier.testTag("tab_history")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (activeTab == 0) {
                    TemplatesTab(templates = RegulationsData.templates) { template ->
                        viewModel.startNewAudit(template)
                    }
                } else {
                    HistoryTab(
                        reports = reports,
                        selectedReportId = selectedReportId,
                        reportAnswers = reportAnswers,
                        onSelectReport = { viewModel.selectReport(it) },
                        onDeleteReport = { viewModel.deleteReport(it) },
                        onNavigateToFindings = onNavigateToFindings
                    )
                }
            }
        }
    }
}

// --- List of Available Templates ---
@Composable
fun TemplatesTab(
    templates: List<ChecklistTemplate>,
    onSelectTemplate: (ChecklistTemplate) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Seleccione una materia de control",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Contiene preguntas específicas de fiscalización y referencias a leyes y decretos reglamentarios argentinos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(templates) { template ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTemplate(template) }
                    .testTag("template_card_${template.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SafetyBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                template.category.uppercase(Locale.getDefault()),
                                fontWeight = FontWeight.Bold,
                                color = SafetyBlue,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            template.title,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            template.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FormatListBulleted,
                                contentDescription = "Items count",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "${template.items.size} puntos de control legal",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        Icons.Default.ArrowForwardIos,
                        contentDescription = "Comenzar",
                        tint = SafetyOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// --- Active Audit Running Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveAuditRunner(
    activeAuditState: com.example.viewmodel.ActiveAuditState,
    onAuditorChanged: (String) -> Unit,
    onCommentsChanged: (String) -> Unit,
    onStatusChanged: (String, String) -> Unit,
    onCommentChanged: (String, String) -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    val template = activeAuditState.template ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(template.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancelar")
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
                .testTag("active_audit_runner"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Inputs Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Datos del Relevamiento Técnico",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = activeAuditState.auditorName,
                            onValueChange = onAuditorChanged,
                            label = { Text("Nombre del Auditor / Inspector") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("audit_auditor_input"),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Auditor") }
                        )

                        OutlinedTextField(
                            value = activeAuditState.comments,
                            onValueChange = onCommentsChanged,
                            label = { Text("Observaciones / Comentarios Generales") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("audit_comments_input"),
                            maxLines = 3,
                            leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = "Observaciones") }
                        )
                    }
                }
            }

            // Questions Section Header
            item {
                Text(
                    "Puntos de Control a Verificar:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Checklist questions items
            items(template.items) { item ->
                val answer = activeAuditState.answers[item.itemId] ?: TempAnswer(item.text, item.category, "")

                QuestionAuditCard(
                    item = item,
                    currentStatus = answer.status,
                    currentComment = answer.comment,
                    onStatusSelected = { onStatusChanged(item.itemId, it) },
                    onCommentChanged = { onCommentChanged(item.itemId, it) }
                )
            }

            // Submit buttons
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_audit"),
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar Reporte")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finalizar e Informar Reporte", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("Descartar e Ir Atrás", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionAuditCard(
    item: ChecklistItemTemplate,
    currentStatus: String,
    currentComment: String,
    onStatusSelected: (String) -> Unit,
    onCommentChanged: (String) -> Unit
) {
    var showCommentBox by remember { mutableStateOf(currentComment.isNotEmpty()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_card_${item.itemId}"),
        colors = CardDefaults.cardColors(
            containerColor = when (currentStatus) {
                "CUMPLE" -> SafetyGreen.copy(alpha = 0.05f)
                "NO_CUMPLE" -> SafetyRed.copy(alpha = 0.05f)
                "NO_APLICA" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            1.dp,
            when (currentStatus) {
                "CUMPLE" -> SafetyGreen.copy(alpha = 0.5f)
                "NO_CUMPLE" -> SafetyRed.copy(alpha = 0.5f)
                "NO_APLICA" -> MaterialTheme.colorScheme.outlineVariant
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Reference legal badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SafetyBlue.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        item.lawReference,
                        fontWeight = FontWeight.Bold,
                        color = SafetyBlue,
                        fontSize = 11.sp
                    )
                }

                Text(
                    item.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                item.text,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selector Button Groups (Cumple, No cumple, No Aplica)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cumple button
                val isCumple = currentStatus == "CUMPLE"
                Button(
                    onClick = { onStatusSelected("CUMPLE") },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("btn_status_cumple_${item.itemId}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCumple) SafetyGreen else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, if (isCumple) SafetyGreen else MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Cumple",
                            tint = if (isCumple) Color.White else SafetyGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Cumple",
                            color = if (isCumple) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // No Cumple button
                val isNoCumple = currentStatus == "NO_CUMPLE"
                Button(
                    onClick = {
                        onStatusSelected("NO_CUMPLE")
                        showCommentBox = true // Automatically encourage commenting for non-compliance
                    },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(44.dp)
                        .testTag("btn_status_nocumple_${item.itemId}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNoCumple) SafetyRed else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, if (isNoCumple) SafetyRed else MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = "No Cumple",
                            tint = if (isNoCumple) Color.White else SafetyRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "No Cumple",
                            color = if (isNoCumple) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // N/A button
                val isNa = currentStatus == "NO_APLICA"
                Button(
                    onClick = { onStatusSelected("NO_APLICA") },
                    modifier = Modifier
                        .weight(0.9f)
                        .height(44.dp)
                        .testTag("btn_status_na_${item.itemId}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNa) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "N/A",
                            tint = if (isNa) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "N/A",
                            color = if (isNa) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Toggle Comment block
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCommentBox = !showCommentBox },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Comment,
                    contentDescription = "Ver Observación",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (showCommentBox) "Ocultar anotación" else "Agregar anotación técnica...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = showCommentBox) {
                OutlinedTextField(
                    value = currentComment,
                    onValueChange = onCommentChanged,
                    placeholder = { Text("Indique desviación, severidad o anotación técnica...", fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("question_comment_${item.itemId}"),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    singleLine = true
                )
            }
        }
    }
}

// --- Historical Completed Audits Tab ---
@Composable
fun HistoryTab(
    reports: List<ChecklistReportEntity>,
    selectedReportId: Int?,
    reportAnswers: List<ChecklistAnswerEntity>,
    onSelectReport: (Int?) -> Unit,
    onDeleteReport: (Int) -> Unit,
    onNavigateToFindings: () -> Unit
) {
    if (selectedReportId != null) {
        val selectedReport = reports.find { it.id == selectedReportId }
        if (selectedReport != null) {
            ReportDetailsViewer(
                report = selectedReport,
                answers = reportAnswers,
                onBack = { onSelectReport(null) },
                onNavigateToFindings = onNavigateToFindings
            )
            return
        }
    }

    if (reports.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = "Vacío",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(54.dp)
                )
                Text(
                    "No hay historial guardado",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Finalice y guarde una inspección técnica desde la pestaña 'Plantillas' para verla aquí de forma permanente.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(reports, key = { it.id }) { report ->
                HistoryReportCard(
                    report = report,
                    onSelect = { onSelectReport(report.id) },
                    onDelete = { onDeleteReport(report.id) }
                )
            }
        }
    }
}

@Composable
fun HistoryReportCard(
    report: ChecklistReportEntity,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(report.dateMillis) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(report.dateMillis))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_report_card_${report.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular score badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                report.scorePercentage >= 85f -> SafetyGreen.copy(alpha = 0.12f)
                                report.scorePercentage >= 65f -> SafetyYellow.copy(alpha = 0.18f)
                                else -> SafetyRed.copy(alpha = 0.12f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        String.format(Locale.getDefault(), "%.0f%%", report.scorePercentage),
                        fontWeight = FontWeight.Black,
                        color = when {
                            report.scorePercentage >= 85f -> SafetyGreen
                            report.scorePercentage >= 65f -> Color(0xFFC79100)
                            else -> SafetyRed
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        report.title,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Auditor: ${report.auditorName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges row for item outputs (Passed, Failed, NA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(SafetyGreen, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${report.passedCount} Cumple", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(SafetyRed, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${report.failedCount} No Cumple", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Gray, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${report.naCount} N/A", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSelect,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("btn_view_report_details_${report.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyBlue),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "Examen", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Revisar Auditoría", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("btn_delete_report_${report.id}")
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// --- Historical Report details viewer subscreen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailsViewer(
    report: ChecklistReportEntity,
    answers: List<ChecklistAnswerEntity>,
    onBack: () -> Unit,
    onNavigateToFindings: () -> Unit
) {
    val dateStr = remember(report.dateMillis) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(report.dateMillis))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados de Inspección", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
                .testTag("report_details_viewer"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            report.title,
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.headlineSmall,
                            color = SafetyBlue
                        )

                        Text("Auditor: ${report.auditorName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text("Fecha de ejecución: $dateStr", style = MaterialTheme.typography.bodyMedium)

                        if (report.comments.isNotEmpty()) {
                            Text(
                                "Comentarios Generales:\n${report.comments}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        // Big compliance block
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Nivel de Cumplimiento Legal:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    String.format(Locale.getDefault(), "%.1f%% Cumple", report.scorePercentage),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = if (report.scorePercentage >= 80f) SafetyGreen else if (report.scorePercentage >= 50f) SafetyYellow else SafetyRed
                                )
                            }

                            // Box counter layout
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Índices absolutos:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${report.passedCount} Corresponden", fontWeight = FontWeight.Bold)
                                Text("${report.failedCount} Desvíos Críticos", color = SafetyRed, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Informational Banner about integration with findings database
                        if (report.failedCount > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SafetyRed.copy(alpha = 0.08f))
                                    .border(BorderStroke(1.dp, SafetyRed.copy(alpha = 0.3f)), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "Integración en Tiempo Real",
                                        fontWeight = FontWeight.Bold,
                                        color = SafetyRed,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Los desvíos críticos detectados fueron cargados automáticamente como 'Hallazgos Activos'.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        "Ir a Hallazgos para gestionarlos",
                                        fontWeight = FontWeight.Bold,
                                        color = SafetyBlue,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier
                                            .clickable { onNavigateToFindings() }
                                            .padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Results sub-heading
            item {
                Text(
                    "Examen Desglosado por Pregunta:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Detailed answers view
            if (answers.isEmpty()) {
                item {
                    Text(
                        "Cargando detalles técnicos de la inspección...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(answers) { answer ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    answer.category,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )

                                // Success status flag
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (answer.status) {
                                            "CUMPLE" -> SafetyGreen
                                            "NO_CUMPLE" -> SafetyRed
                                            else -> Color.Gray
                                        }
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        answer.status.replace("_", " "),
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                answer.questionText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (answer.comment.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                        .padding(8.dp)
                                ) {
                                    Icon(Icons.Default.Comment, contentDescription = "Nota", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Anotación: ${answer.comment}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
