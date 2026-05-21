package com.example.a216839_wan_lab5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a216839_wan_lab5.data.repository.AppointmentRepository
import com.example.a216839_wan_lab5.data.repository.BmiRepository
import com.example.a216839_wan_lab5.data.repository.HealthProfileRepository

class HealthProfileViewModelFactory(
    private val repository: HealthProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

class AppointmentViewModelFactory(
    private val repository: AppointmentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

class BmiViewModelFactory(
    private val repository: BmiRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BmiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BmiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}