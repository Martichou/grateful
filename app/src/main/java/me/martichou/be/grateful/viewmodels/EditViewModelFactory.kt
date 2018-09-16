package me.martichou.be.grateful.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import me.martichou.be.grateful.repository.NotesRepository

class EditViewModelFactory(
    private val repository: NotesRepository,
    private val id: Long
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = EditViewModel(repository, id) as T
}