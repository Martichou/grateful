package me.martichou.be.grateful.repository

import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.NotesDao
import me.martichou.be.grateful.utilities.runOnIoThread


class NotesRepository private constructor(private val notesDao: NotesDao) {

    /**
     * Return all notes from db
     */
    fun getallNotes() = notesDao.getAllNotes()

    /**
     * Return one note
     * @param noteId = note id of which we want to see
     */
    fun getThisNote(noteId: Long) = notesDao.getThisNote(noteId)

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: NotesRepository? = null

        fun getInstance(notesDao: NotesDao) =
                instance ?: synchronized(this) {
                    instance ?: NotesRepository(notesDao).also { instance = it }
                }
    }

    /**
     * Insert a new note using a different thread
     * to avoid hang.
     */
    fun insert(notes: Notes) {
        runOnIoThread {
            notesDao.insertNotes(notes)
        }
    }
}