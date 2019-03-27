package me.martichou.be.grateful.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.data.model.Notes
import me.martichou.be.grateful.data.repository.NotesRepository
import me.martichou.be.grateful.view.adapter.NotesAdapter

class MainViewModel internal constructor(notesRepository: NotesRepository) : ViewModel() {

    val recentNotesList = MediatorLiveData<List<Notes>>().apply {
        addSource(notesRepository.getAllNote(), this::setValue)
    }
    val adapter = NotesAdapter()

}
