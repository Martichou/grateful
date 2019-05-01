package me.martichou.be.grateful.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.db.NotesDao
import me.martichou.be.grateful.vo.Notes
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class NotesRepository
@Inject constructor(private val notesDao: NotesDao) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

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
    fun insert(notes: Notes) {
        launch(Dispatchers.IO) { notesDao.insertNote(notes) }
    }

    /**
     * Update a note using Notes
     */
    fun update(notes: Notes) {
        launch(Dispatchers.IO) { notesDao.updateNote(notes) }
    }

    /**
     * Delete a note using Id
     */
    fun deleteById(noteId: Long) {
        launch(Dispatchers.IO) { notesDao.deleteNoteById(noteId) }
    }

    /**
     * Delete all
     */
    fun nukeIt() {
        launch(Dispatchers.IO) { notesDao.nukeTable() }
    }
}