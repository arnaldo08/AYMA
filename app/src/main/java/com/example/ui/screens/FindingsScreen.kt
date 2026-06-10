package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FindingEntity
import com.example.ui.theme.*
import com.example.viewmodel.SecurityViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindingsScreen(viewModel: SecurityViewModel) {
    val findings by viewModel.allFindings.collectAsState()

    var showReportForm by remember { mutableStateOf(false) }

    // Form inputs state
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var riskLevel by remember { mutableStateOf("ALTO") } // BAJO, MEDIO, ALTO, CRITICO
    var regulatoryReference by remember { mutableStateOf("") }
    var actionRequired by remember { mutableStateOf("") }
    var inspectorName by remember { mutableStateOf("") }

    var selectedStatusFilter by remember { mutableStateOf("TODOS") } // TODOS, PENDIENTE, EN_PROCESO, SOLUCIONADO

    val filteredFindings = remember(findings, selectedStatusFilter) {
        if (selectedStatusFilter == "TODOS") findings
        else findings.filter { it.status == selectedStatusFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hallazgos de Campo", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        floatingActionButton = {
            if (!showReportForm) {
                FloatingActionButton(
                    onClick = { showReportForm = true },
                    containerColor = SafetyOrange,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_finding")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar Hallazgo")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Filters - Status Selector Tab Row
            ScrollableTabRow(
                selectedTabIndex = when (selectedStatusFilter) {
                    "TODOS" -> 0
                    "PENDIENTE" -> 1
                    "EN_PROCESO" -> 2
                    "SOLUCIONADO" -> 3
                    else -> 0
                },
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedStatusFilter == "TODOS",
                    onClick = { selectedStatusFilter = "TODOS" },
                    text = { Text("Todos (${findings.size})") },
                    modifier = Modifier.testTag("filter_all_findings")
                )
                Tab(
                    selected = selectedStatusFilter == "PENDIENTE",
                    onClick = { selectedStatusFilter = "PENDIENTE" },
                    text = { Text("Pendientes (${findings.count { it.status == "PENDIENTE" }})") },
                    modifier = Modifier.testTag("filter_pending_findings")
                )
                Tab(
                    selected = selectedStatusFilter == "EN_PROCESO",
                    onClick = { selectedStatusFilter = "EN_PROCESO" },
                    text = { Text("En Proceso (${findings.count { it.status == "EN_PROCESO" }})") }
                )
                Tab(
                    selected = selectedStatusFilter == "SOLUCIONADO",
                    onClick = { selectedStatusFilter = "SOLUCIONADO" },
                    text = { Text("Resueltos (${findings.count { it.status == "SOLUCIONADO" }})") }
                )
            }

            // Expandable Form Sheet
            AnimatedVisibility(
                visible = showReportForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("report_finding_form"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Registrar Desvío de Campo",
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.titleMedium,
                                color = SafetyBlue
                            )
                            IconButton(onClick = { showReportForm = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancelar")
                            }
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título / Desvío detectado (Ej. Fuga de disyuntor)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_finding_title"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción de la condición insegura") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                label = { Text("Sector / Ubicación") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = regulatoryReference,
                                onValueChange = { regulatoryReference = it },
                                label = { Text("Ref. Legal (Ej. Art 95)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        // Risk selection badges row
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Nivel de gravedad / Riesgo:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "BAJO" to SafetyGreen,
                                    "MEDIO" to SafetyYellow,
                                    "ALTO" to SafetyOrange,
                                    "CRITICO" to SafetyRed
                                ).forEach { (level, color) ->
                                    val isSelected = riskLevel == level
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { riskLevel = level }
                                            .testTag("risk_badge_$level"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) color else color.copy(alpha = 0.1f)
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                level,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = if (isSelected) Color.White else color
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = actionRequired,
                            onValueChange = { actionRequired = it },
                            label = { Text("Acción Correctiva Sugerida (Medida preventiva)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = inspectorName,
                            onValueChange = { inspectorName = it },
                            label = { Text("Nombre del Inspector de Campo") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.PersonSearch, contentDescription = "Inspector") }
                        )

                        Button(
                            onClick = {
                                viewModel.logNewFinding(
                                    title = title,
                                    description = description,
                                    location = location,
                                    riskLevel = riskLevel,
                                    regulatoryReference = regulatoryReference,
                                    actionRequired = actionRequired,
                                    inspectorName = inspectorName
                                ) {
                                    // Reset and Close
                                    title = ""
                                    description = ""
                                    location = ""
                                    riskLevel = "ALTO"
                                    regulatoryReference = ""
                                    actionRequired = ""
                                    inspectorName = ""
                                    showReportForm = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_submit_finding"),
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrange),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.PostAdd, contentDescription = "Guardar desvío")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Registrar Desvío Técnicamente", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // List of Current Findings
            if (filteredFindings.isEmpty()) {
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
                            Icons.Default.OfflinePin,
                            contentDescription = "Sin desvíos",
                            tint = SafetyGreen.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            "Sin desvíos en esta categoría",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "¡Felicidades! Todo el establecimiento cumple con el estándar y no registra hallazgos activos.",
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
                    items(filteredFindings, key = { it.id }) { finding ->
                        FindingCard(
                            finding = finding,
                            onStatusChange = { newStatus ->
                                viewModel.updateFindingStatus(finding.id, newStatus)
                            },
                            onDelete = {
                                viewModel.deleteFinding(finding.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FindingCard(
    finding: FindingEntity,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(finding.dateMillis) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(finding.dateMillis))
    }

    val riskColor = when (finding.riskLevel) {
        "BAJO" -> SafetyGreen
        "MEDIO" -> SafetyYellow
        "ALTO" -> SafetyOrange
        "CRITICO" -> SafetyRed
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("finding_card_${finding.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (finding.status == "SOLUCIONADO") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Side bar accent for hazard level
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .background(riskColor)
                    .height(140.dp) // Minimum height assurance
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Topic tag and status badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(riskColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "RIESGO ${finding.riskLevel}",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = if (finding.riskLevel == "MEDIO") Color(0xFF8F6B00) else riskColor
                        )
                    }

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (finding.status) {
                                    "PENDIENTE" -> SafetyRed.copy(alpha = 0.12f)
                                    "EN_PROCESO" -> SafetyYellow.copy(alpha = 0.18f)
                                    "SOLUCIONADO" -> SafetyGreen.copy(alpha = 0.12f)
                                    else -> Color.Gray
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            finding.status.replace("_", " "),
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = when (finding.status) {
                                "PENDIENTE" -> SafetyRed
                                "EN_PROCESO" -> Color(0xFFC79100)
                                "SOLUCIONADO" -> SafetyGreen
                                else -> Color.White
                            }
                        )
                    }
                }

                // Title & Description
                Text(
                    finding.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (finding.status == "SOLUCIONADO") TextDecoration.LineThrough else null
                )

                Text(
                    finding.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Audit metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = "Ubicación", modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(finding.location, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
                    }

                    if (finding.regulatoryReference.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = "Ref. Legal", modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(finding.regulatoryReference, fontSize = 11.sp, color = SafetyBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Remedial Correction Action Requirement
                if (finding.actionRequired.isNotEmpty() && finding.status != "SOLUCIONADO") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                "Medida Correctiva Preventiva:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                finding.actionRequired,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Details footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Por ${finding.inspectorName} • $dateStr",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // State Controls Buttons
                if (finding.status != "SOLUCIONADO") {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (finding.status == "PENDIENTE") {
                            Button(
                                onClick = { onStatusChange("EN_PROCESO") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .testTag("btn_transition_progress_${finding.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = SafetyYellow),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Autorenew, contentDescription = "En Proceso", modifier = Modifier.size(14.dp), tint = Color.Black)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Iniciar Proceso", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }

                        Button(
                            onClick = { onStatusChange("SOLUCIONADO") },
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("btn_transition_solve_${finding.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyGreen),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DoneAll, contentDescription = "Solucionado", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cerrar desvío", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
