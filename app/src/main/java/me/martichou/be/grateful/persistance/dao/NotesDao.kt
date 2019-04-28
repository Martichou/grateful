package me.martichou.be.grateful.persistance.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.martichou.be.grateful.data.model.Notes

@Dao
interface NotesDao {

    /**
     * Return all note without exception
     */
    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getAllNote(): LiveData<List<Notes>>

    /**
     * Return the note with id = noteId
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getThisNote(noteId: Long): LiveData<Notes>

    /**
     * Insert note object into database
     */
    @Insert
    fun insertNote(notes: Notes)

    /**
     * Update note by using a note object
     */
    @Update
    fun updateNote(notes: Notes)

    /**
     * Delete a node using his ID
     */
    @Query("DELETE FROM notes WHERE id =:noteId")
    fun deleteNoteById(noteId: Long)

    /**
     * Delete all
     */
    @Query("DELETE FROM notes")
    fun nukeTable()
}