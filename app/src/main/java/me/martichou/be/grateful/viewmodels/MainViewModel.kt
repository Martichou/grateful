package me.martichou.be.grateful.viewmodels

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository

class MainViewModel internal constructor(
        private val notesRepository: NotesRepository
) : ViewModel() {

    private val notesList = MediatorLiveData<List<Notes>>()

    init {
        notesList.addSource(notesRepository.getallNotes(), notesList::setValue)
    }

    /**
     * Get all notes existing in the db
     */
    fun getAllNotes() = notesList

    /**
     * Insert a new note in the db
     */
    fun insertNote(notes: Notes) = notesRepository.insert(notes)

    /**
     * Update the content of a note from the db
     */
    fun updateNote(notes: Notes) = notesRepository.update(notes)

    /**
     * Delete a note from the database
     */
    fun deleteNote(notes: Notes) = notesRepository.delete(notes)

    /**
     * Delete all notes
     */
    fun deleteAll() = notesRepository.deleteAll()

}
