package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository

class MainViewModel internal constructor(notesRepository: NotesRepository) : ViewModel() {

    val recentNotesList = MediatorLiveData<List<Notes>>()

    /**
     * Fill in the notesList & adapter
     */
    init {
        recentNotesList.addSource(notesRepository.getAllNote(), recentNotesList::setValue)
    }

    fun getNotes() = recentNotesList
}
