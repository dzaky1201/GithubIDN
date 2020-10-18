package com.dzakyhdr.githubidn.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dzakyhdr.githubidn.repository.DataRepository

class HomeViewModelFactory(
    val app: Application,
    val dataRepository: DataRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(app, dataRepository) as T
    }
}