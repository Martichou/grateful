package me.martichou.be.grateful.utilities

import android.content.Context
import me.martichou.be.grateful.data.AppDatabase
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.viewmodels.MainViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getNotesRepository(context: Context): NotesRepository {
        return NotesRepository.getInstance(AppDatabase.getInstance(context).notesDao())
    }

    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        val repository = getNotesRepository(context)
        return MainViewModelFactory(repository)
    }

}