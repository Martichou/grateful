package me.martichou.be.grateful.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<Notes>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getThisNote(noteId: Long): LiveData<Notes>

    @Insert
    fun insertNotes(notes: Notes)
}