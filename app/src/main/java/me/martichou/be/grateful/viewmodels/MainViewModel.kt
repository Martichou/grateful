package me.martichou.be.grateful.viewmodels

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository

class MainViewModel internal constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val notesList = MediatorLiveData<List<Notes>>()

    /**
     * Fill in the notesList
     */
    init {
        notesList.addSource(notesRepository.getallNotes(), notesList::setValue)
    }

    /**
     * Get all notes existing in the db
     */
    fun getAllNotes() = notesList

    /**
     * Delete all notes
     */
    fun deleteAll() = notesRepository.deleteAll()
}
