package me.martichou.be.grateful.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
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
