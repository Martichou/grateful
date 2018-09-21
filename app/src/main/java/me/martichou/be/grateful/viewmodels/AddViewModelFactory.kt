package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.martichou.be.grateful.repository.NotesRepository

class AddViewModelFactory(private val repository: NotesRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = AddViewModel(repository) as T
}