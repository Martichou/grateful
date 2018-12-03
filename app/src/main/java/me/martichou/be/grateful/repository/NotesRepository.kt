package me.martichou.be.grateful.repository

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.NotesDao
import me.martichou.be.grateful.utilities.runOnIoThread

class NotesRepository private constructor(private val notesDao: NotesDao) {

    companion object {
        @Volatile
        private var instance: NotesRepository? = null

        fun getInstance(notesDao: NotesDao) = instance ?: synchronized(this) {
            instance ?: NotesRepository(notesDao).also { instance = it }
        }
    }

    /**
     * Return all notes from db
     */
    fun getallNotes() = notesDao.getAllNotes()

    /**
     * Return one note in Livedata
     * @param noteId = note id of which we want to see
     */
    fun getThisNote(noteId: Long) = notesDao.getThisNote(noteId)

    /**
     * Insert a note using Notes
     */
    suspend fun insert(notes: Notes) {
        withContext(IO){
            notesDao.insertNote(notes)
        }
    }

    /**
     * Update a note using Notes
     */
    suspend fun update(notes: Notes) {
        withContext(IO){
            notesDao.updateNote(notes)
        }
    }

    /**
     * Delete a note using Notes
     */
    suspend fun delete(notes: Notes) {
        withContext(IO){
            notesDao.deleteNote(notes)
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