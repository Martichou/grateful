package me.martichou.be.grateful.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Notes>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getThisNote(noteId: Long): LiveData<Notes>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getThisNoteStatic(noteId: Long): Notes

    @Insert
    fun insertNote(notes: Notes)

    @Update
    fun updateNote(notes: Notes)

    @Delete
    fun deleteNote(notes: Notes)

    @Query("DELETE FROM notes ")
    fun deleteAll()
}