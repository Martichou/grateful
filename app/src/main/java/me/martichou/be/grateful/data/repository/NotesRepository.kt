package me.martichou.be.grateful.data.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.NotesDao

class NotesRepository private constructor(private val notesDao: NotesDao) {

    companion object {
        @Volatile
        private var instance: NotesRepository? = null

        fun getInstance(notesDao: NotesDao) = instance ?: synchronized(this) {
            instance ?: NotesRepository(notesDao).also { instance = it }
        }
    }

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
    suspend fun insert(notes: Notes) {
        withContext(IO) {
            notesDao.insertNote(notes)
        }
    }

    /**
     * Update a note using Notes
     */
    suspend fun update(notes: Notes) {
        withContext(IO) {
            notesDao.updateNote(notes)
        }
    }

    /**
     * Delete a note using Id
     */
    suspend fun deleteById(noteId: Long) {
        withContext(IO) {
            notesDao.deleteNoteById(noteId)
        }
    }
}