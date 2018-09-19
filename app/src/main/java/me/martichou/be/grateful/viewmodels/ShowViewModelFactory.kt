package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.martichou.be.grateful.repository.NotesRepository

class ShowViewModelFactory(
    private val repository: NotesRepository,
    private val id: Long
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = ShowViewModel(repository, id) as T
}