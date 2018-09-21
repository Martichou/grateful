package me.martichou.be.grateful.utilities

import android.content.Context
import me.martichou.be.grateful.data.AppDatabase
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.viewmodels.AddViewModelFactory
import me.martichou.be.grateful.viewmodels.MainViewModelFactory
import me.martichou.be.grateful.viewmodels.ShowViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    /**
     * Return the repository
     */
    private fun getNotesRepository(context: Context): NotesRepository {
        return NotesRepository.getInstance(AppDatabase.getInstance(context).notesDao())
    }

    /**
     * Provide the MainViewModel initialized
     */
    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        val repository = getNotesRepository(context)
        return MainViewModelFactory(repository)
    }

    /**
     * Provide the AddViewModel initialized
     */
    fun provideAddViewModelFactory(context: Context): AddViewModelFactory {
        val repository = getNotesRepository(context)
        return AddViewModelFactory(repository)
    }

    /**
     * Provide the ShowViewModel initialized
     * with args -> id
     */
    fun provideShowViewModelFactory(context: Context, id: Long): ShowViewModelFactory {
        val repository = getNotesRepository(context)
        return ShowViewModelFactory(repository, id)
    }
}