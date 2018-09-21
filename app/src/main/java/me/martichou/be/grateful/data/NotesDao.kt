package me.martichou.be.grateful.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Query("SELECT id, title, image, color, date, location  FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<NotesMinimal>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getThisNote(noteId: Long): LiveData<Notes>

    @Insert
    fun insertNote(notes: Notes)

    @Update
    fun updateNote(notes: Notes)

    @Delete
    fun deleteNote(notes: Notes)

    @Query("DELETE FROM notes WHERE id =:noteId")
    fun deleteNoteById(noteId: Long)
}