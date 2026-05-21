package com.example.a216839_wan_lab5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a216839_wan_lab5.data.entity.HealthProfileEntity
import com.example.a216839_wan_lab5.data.repository.HealthProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HealthProfileViewModel(
    private val repository: HealthProfileRepository
) : ViewModel() {

    // Expose profile as StateFlow so UI collects reactively
    val profile: StateFlow<HealthProfileEntity?> = repository.profile
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun saveProfile(entity: HealthProfileEntity) {
        viewModelScope.launch {
            repository.saveProfile(entity)
        }
    }

    fun isProfileComplete(): Boolean {
        val p = profile.value
        return p != null && p.fullName.isNotBlank() && p.icNumber.isNotBlank()
    }
}