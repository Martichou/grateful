package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.adapters.NotesAdapter
import me.martichou.be.grateful.data.NotesMinimal
import me.martichou.be.grateful.repository.NotesRepository

class MainViewModel internal constructor(notesRepository: NotesRepository) : ViewModel() {

    val notesList = MediatorLiveData<List<NotesMinimal>>()
    val adapter: NotesAdapter

    /**
     * Fill in the notesList & adapter
     */
    init {
        notesList.addSource(notesRepository.getallNotes(), notesList::setValue)
        adapter = NotesAdapter()
    }
}
