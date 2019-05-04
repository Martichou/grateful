package me.martichou.be.grateful.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.db.NotesDao
import me.martichou.be.grateful.vo.Notes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepository
@Inject constructor(private val notesDao: NotesDao) {

    /**
     * Return all note without exception
     */
    fun getAllNote() = notesDao.getAllNote()

    /**
     * Return one note in Livedata
     * @param noteId = note id of which we want to see
     */
    fun getThisNote(noteId: Long) = notesDao.getThisNote(noteId)

    /**
     * Insert a note using Notes
     */
    fun insert(notes: Notes) {
        CoroutineScope(Dispatchers.IO).launch { notesDao.insertNote(notes) }
    }

    /**
     * Update a note using Notes
     */
    fun update(notes: Notes) {
        CoroutineScope(Dispatchers.IO).launch { notesDao.updateNote(notes) }
    }

    /**
     * Delete a note using Id
     */
    fun deleteById(noteId: Long) {
        CoroutineScope(Dispatchers.IO).launch { notesDao.deleteNoteById(noteId) }
    }

    /**
     * Delete all
     */
    fun nukeIt() {
        CoroutineScope(Dispatchers.IO).launch { notesDao.nukeTable() }
    }
}