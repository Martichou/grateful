package me.martichou.be.grateful.viewmodels

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.repository.NotesRepository
import me.martichou.be.grateful.recyclerView.NotesAdapter

class MainViewModel internal constructor(notesRepository: NotesRepository) : ViewModel() {

    val recentNotesList = MediatorLiveData<List<Notes>>()
    val adapter = NotesAdapter()
    val hasNotes = ObservableBoolean(false)

    /**
     * Fill in the notesList & adapter
     */
    init {
        recentNotesList.addSource(notesRepository.getAllNote()) {
            recentNotesList.value = it
            hasNotes.set(true)
        }
    }
}
