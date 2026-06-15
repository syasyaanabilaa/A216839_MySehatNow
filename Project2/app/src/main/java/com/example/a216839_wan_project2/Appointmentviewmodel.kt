package com.example.a216839_wan_project2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a216839_wan_project2.data.entity.AppointmentEntity
import com.example.a216839_wan_project2.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val repository: AppointmentRepository
) : ViewModel() {

    val appointments: StateFlow<List<AppointmentEntity>> = repository.allAppointments
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val upcomingCount: StateFlow<Int> = repository.upcomingCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val completedCount: StateFlow<Int> = repository.completedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun addAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch { repository.insert(appointment) }
    }

    fun deleteAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch { repository.delete(appointment) }
    }

    fun updateAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch { repository.update(appointment) }
    }
}