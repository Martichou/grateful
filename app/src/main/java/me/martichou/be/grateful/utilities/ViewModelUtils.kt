package me.martichou.be.grateful.utilities

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import me.martichou.be.grateful.data.AppDatabase
import me.martichou.be.grateful.data.repository.NotesRepository
import me.martichou.be.grateful.viewmodels.BaseViewModelFactory

/**
 * Return the repository
 */
fun getNotesRepository(context: Context): NotesRepository {
    return NotesRepository.getInstance(AppDatabase.getInstance(context).notesDao())
}

inline fun <reified T : ViewModel> Fragment.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}