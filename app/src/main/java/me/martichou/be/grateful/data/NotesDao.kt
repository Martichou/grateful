package me.martichou.be.grateful.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDao {

    @Query("SELECT id, title, image, color, date, location  FROM notes WHERE NOT dateToSearch = :date ORDER BY id DESC")
    fun getAllNotes(date: String): LiveData<List<NotesMinimal>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getThisNote(noteId: Long): LiveData<Notes>

    @Query("SELECT * FROM notes WHERE dateToSearch = :date LIMIT 1")
    fun getNoteAtDate(date: String): LiveData<Notes>

    @Insert
    fun insertNote(notes: Notes)

    @Update
    fun updateNote(notes: Notes)

    @Delete
    fun deleteNote(notes: Notes)

    @Query("DELETE FROM notes WHERE id =:noteId")
    fun deleteNoteById(noteId: Long)
}