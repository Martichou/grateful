package me.martichou.be.grateful.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.martichou.be.grateful.vo.Notes

@Dao
abstract class NotesDao {

    /**
     * Return all note without exception
     */
    @Query("SELECT * FROM notes ORDER BY date DESC")
    abstract fun getAllNote(): LiveData<List<Notes>>

    /**
     * Return the note with id = noteId
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    abstract fun getThisNote(noteId: Long): LiveData<Notes>

    /**
     * Insert note object into database
     */
    @Insert
    abstract fun insertNote(notes: Notes)

    /**
     * Update note by using a note object
     */
    @Update
    abstract fun updateNote(notes: Notes)

    /**
     * Delete a node using his ID
     */
    @Query("DELETE FROM notes WHERE id =:noteId")
    abstract fun deleteNoteById(noteId: Long)

    /**
     * Delete all
     */
    @Query("DELETE FROM notes")
    abstract fun nukeTable()
}