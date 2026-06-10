package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.ChecklistAnswerEntity
import com.example.data.model.ChecklistReportEntity
import com.example.data.model.FindingEntity
import com.example.data.repository.SecurityRepository
import com.example.data.staticdata.ChecklistItemTemplate
import com.example.data.staticdata.ChecklistTemplate
import com.example.data.staticdata.RegulationsData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TempAnswer(
    val questionText: String,
    val category: String,
    val status: String, // "CUMPLE", "NO_CUMPLE", "NO_APLICA", "" (pending)
    val comment: String = "",
    val lawReference: String = ""
)

data class ActiveAuditState(
    val template: ChecklistTemplate? = null,
    val auditorName: String = "",
    val comments: String = "",
    val answers: Map<String, TempAnswer> = emptyMap() // itemId -> TempAnswer
)

class SecurityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SecurityRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SecurityRepository(database.securityDao())
    }

    // --- Database Flow Expositions ---
    val allReports: StateFlow<List<ChecklistReportEntity>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFindings: StateFlow<List<FindingEntity>> = repository.allFindings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Search & Filters for laws ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedLawCategory = MutableStateFlow("TODOS") // "TODOS", "GENERAL", "CONSTRUCCION", "AGRO", "PROTOCOLOS"
    val selectedLawCategory = _selectedLawCategory.asStateFlow()

    val filteredRegulations = combine(
        _searchQuery,
        _selectedLawCategory
    ) { query, category ->
        RegulationsData.regulations.filter { reg ->
            val matchesQuery = reg.title.contains(query, ignoreCase = true) ||
                    reg.subtitle.contains(query, ignoreCase = true) ||
                    reg.description.contains(query, ignoreCase = true) ||
                    reg.keyPoints.any { it.contains(query, ignoreCase = true) }
            
            val matchesCategory = category == "TODOS" || reg.category == category

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RegulationsData.regulations)

    // --- In-Memory Active Checklist Audit State ---
    private val _activeAudit = MutableStateFlow<ActiveAuditState?>(null)
    val activeAudit = _activeAudit.asStateFlow()

    // --- Selected Historical Report Detail & Flow ---
    private val _selectedReportId = MutableStateFlow<Int?>(null)
    val selectedReportId = _selectedReportId.asStateFlow()

    val selectedReportAnswers: StateFlow<List<ChecklistAnswerEntity>> = _selectedReportId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getAnswersForReport(id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLawCategory(category: String) {
        _selectedLawCategory.value = category
    }

    // --- Active Checklist flow methods ---
    fun startNewAudit(template: ChecklistTemplate) {
        val initialAnswers = template.items.associate { item ->
            item.itemId to TempAnswer(
                questionText = item.text,
                category = item.category,
                status = "", // Unanswered on start
                comment = "",
                lawReference = item.lawReference
            )
        }
        _activeAudit.value = ActiveAuditState(
            template = template,
            answers = initialAnswers
        )
    }

    fun updateAuditorName(name: String) {
        _activeAudit.value = _activeAudit.value?.copy(auditorName = name)
    }

    fun updateChecklistComments(comments: String) {
        _activeAudit.value = _activeAudit.value?.copy(comments = comments)
    }

    fun updateAnswerStatus(itemId: String, status: String) {
        val current = _activeAudit.value ?: return
        val currentAnswer = current.answers[itemId] ?: return
        val updatedAnswers = current.answers.toMutableMap().apply {
            put(itemId, currentAnswer.copy(status = status))
        }
        _activeAudit.value = current.copy(answers = updatedAnswers)
    }

    fun updateAnswerComment(itemId: String, comment: String) {
        val current = _activeAudit.value ?: return
        val currentAnswer = current.answers[itemId] ?: return
        val updatedAnswers = current.answers.toMutableMap().apply {
            put(itemId, currentAnswer.copy(comment = comment))
        }
        _activeAudit.value = current.copy(answers = updatedAnswers)
    }

    fun cancelActiveAudit() {
        _activeAudit.value = null
    }

    fun submitActiveAudit(onSuccess: () -> Unit) {
        val current = _activeAudit.value ?: return
        val template = current.template ?: return

        viewModelScope.launch {
            // Count outcomes
            var passed = 0
            var failed = 0
            var na = 0
            
            val answersList = current.answers.map { (itemId, tempAns) ->
                when (tempAns.status) {
                    "CUMPLE" -> passed++
                    "NO_CUMPLE" -> failed++
                    "NO_APLICA" -> na++
                }
                ChecklistAnswerEntity(
                    reportId = 0, // Assigned inside Repository during insertion
                    questionId = itemId,
                    questionText = tempAns.questionText,
                    category = tempAns.category,
                    status = if (tempAns.status.isEmpty()) "NO_APLICA" else tempAns.status,
                    comment = tempAns.comment
                )
            }

            // Calculate score based only on relevant answers (CUMPLE vs NO_CUMPLE)
            val relevantCount = passed + failed
            val scorePct = if (relevantCount > 0) {
                (passed.toFloat() / relevantCount.toFloat()) * 100f
            } else {
                100f // Default to 100% compliance if all are NA or empty
            }

            val report = ChecklistReportEntity(
                title = template.title,
                auditorName = current.auditorName.ifEmpty { "Auditor Anónimo" },
                dateMillis = System.currentTimeMillis(),
                comments = current.comments,
                scorePercentage = scorePct,
                passedCount = passed,
                failedCount = failed,
                naCount = na + (answersList.size - (passed + failed + na)) // Treat unselected as N/A
            )

            repository.saveChecklistReport(report, answersList)
            _activeAudit.value = null // Reset audit
            onSuccess()
        }
    }

    // --- Report Detail ---
    fun selectReport(id: Int?) {
        _selectedReportId.value = id
    }

    fun deleteReport(id: Int) {
        viewModelScope.launch {
            repository.deleteFullReport(id)
            if (_selectedReportId.value == id) {
                _selectedReportId.value = null
            }
        }
    }

    // --- Finding actions ---
    fun logNewFinding(
        title: String,
        description: String,
        location: String,
        riskLevel: String,
        regulatoryReference: String,
        actionRequired: String,
        inspectorName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val finding = FindingEntity(
                title = title.ifEmpty { "Hallazgo sin título" },
                description = description,
                location = location.ifEmpty { "General/Establecimiento" },
                riskLevel = riskLevel,
                regulatoryReference = regulatoryReference.ifEmpty { "Sin referencia" },
                actionRequired = actionRequired,
                status = "PENDIENTE",
                dateMillis = System.currentTimeMillis(),
                inspectorName = inspectorName.ifEmpty { "Inspector General" }
            )
            repository.saveFinding(finding)
            onSuccess()
        }
    }

    fun updateFindingStatus(id: Int, status: String) {
        viewModelScope.launch {
            repository.updateFindingStatus(id, status)
        }
    }

    /**
     * Auto-generates a mock finding based on a non-complying checklist response to show integration!
     */
    fun logFindingFromChecklist(
        title: String,
        description: String,
        location: String,
        lawRef: String,
        inspector: String
    ) {
        viewModelScope.launch {
            val finding = FindingEntity(
                title = title,
                description = description,
                location = location,
                riskLevel = "ALTO", // Default to ALTO from audits
                regulatoryReference = lawRef,
                actionRequired = "Subsanar de inmediato según indicación de inspección técnica legal.",
                status = "PENDIENTE",
                dateMillis = System.currentTimeMillis(),
                inspectorName = inspector
            )
            repository.saveFinding(finding)
        }
    }

    fun deleteFinding(id: Int) {
        viewModelScope.launch {
            repository.deleteFinding(id)
        }
    }
}
