package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository

class ShowViewModel internal constructor(
    notesRepository: NotesRepository,
    id: Long
) : ViewModel() {

    /**
     * Get the note from the db
     */
    var note: LiveData<Notes> = notesRepository.getThisNote(id)
}
