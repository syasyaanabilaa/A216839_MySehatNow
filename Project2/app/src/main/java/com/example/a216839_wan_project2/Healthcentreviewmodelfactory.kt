package com.example.a216839_wan_project2.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.a216839_wan_project2.data.repository.SavedClinicRepository

class HealthCentreViewModelFactory(
    private val app       : Application,
    private val savedRepo : SavedClinicRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthCentreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthCentreViewModel(app, savedRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
