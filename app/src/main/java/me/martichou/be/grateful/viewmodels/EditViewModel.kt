package me.martichou.be.grateful.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository

class EditViewModel internal constructor(
    private val notesRepository: NotesRepository,
    id: Long
) : ViewModel() {

    var hasPhotoUpdated: Boolean = false

    /**
     * Get the note from the db
     */
    var note: LiveData<Notes> = notesRepository.getThisNote(id)

    /**
     * Get the static note
     */
    fun getThisNoteStatic(noteId: Long) = notesRepository.getThisNoteStatic(noteId)

    /**
     * Update the note
     */
    fun updateNote(note: Notes) = notesRepository.update(note)

    /**
     * Delete the note
     */
    fun deleteNote(note: Notes) = notesRepository.delete(note)
}
