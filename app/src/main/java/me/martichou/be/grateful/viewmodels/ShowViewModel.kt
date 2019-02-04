package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.repository.NotesRepository

class ShowViewModel internal constructor(notesRepository: NotesRepository, id: Long) :
        ViewModel() {

    var note = MediatorLiveData<Notes>()

    /**
     * Fill in the note var
     */
    init {
        note.addSource(notesRepository.getThisNote(id), note::setValue)
    }
}
