package com.example.a216839_wan_lab5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a216839_wan_lab5.data.entity.BmiRecordEntity
import com.example.a216839_wan_lab5.data.repository.BmiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.pow

// ── BMI RESULT MODEL ──────────────────────────────────────────────────────────
data class BmiResult(
    val bmi      : Double,
    val category : String,
    val advice   : String,
    val colorHex : String,   // for zone indicator
    val bgColorHex: String
)

class BmiViewModel(
    private val repository: BmiRepository
) : ViewModel() {

    // ── Current calculation result ────────────────────────────────────────────
    private val _currentResult = MutableStateFlow<BmiResult?>(null)
    val currentResult: StateFlow<BmiResult?> = _currentResult.asStateFlow()

    // ── History from Room ─────────────────────────────────────────────────────
    val bmiHistory: StateFlow<List<BmiRecordEntity>> = repository.allRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val latestRecord: StateFlow<BmiRecordEntity?> = repository.latestRecord
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // ── Calculate and persist ─────────────────────────────────────────────────
    fun calculate(weightKg: Double, heightCm: Double) {
        val heightM = heightCm / 100.0
        val bmi     = Math.round((weightKg / heightM.pow(2)) * 10.0) / 10.0

        val result = when {
            bmi < 18.5 -> BmiResult(bmi, "Underweight",   "You may need to increase your caloric intake. Consider consulting a dietitian for a personalised meal plan.",   "#1565C0", "#E3F2FD")
            bmi < 25.0 -> BmiResult(bmi, "Normal Weight", "Great job! Maintain your healthy weight through a balanced diet and at least 30 minutes of exercise most days.", "#2E7D32", "#E8F5E9")
            bmi < 30.0 -> BmiResult(bmi, "Overweight",    "Consider a balanced diet and more physical activity. A 30-minute brisk walk daily can make a big difference.",   "#E65100", "#FFF3E0")
            else       -> BmiResult(bmi, "Obese",          "Please consult a healthcare professional for a personalised weight management plan and medical advice.",          "#C62828", "#FFEBEE")
        }

        _currentResult.value = result

        // Persist to Room
        viewModelScope.launch {
            repository.insert(
                BmiRecordEntity(
                    weightKg  = weightKg,
                    heightCm  = heightCm,
                    bmi       = bmi,
                    category  = result.category,
                    recordedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun clearResult() { _currentResult.value = null }

    fun deleteRecord(record: BmiRecordEntity) {
        viewModelScope.launch { repository.delete(record) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.deleteAll() }
    }
}