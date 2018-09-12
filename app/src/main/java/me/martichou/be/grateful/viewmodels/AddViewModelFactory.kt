package me.martichou.be.grateful.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import me.martichou.be.grateful.repository.NotesRepository

class AddViewModelFactory(
    private val repository: NotesRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = AddViewModel(repository) as T
}