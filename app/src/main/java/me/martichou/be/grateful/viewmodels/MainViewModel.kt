package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.adapters.NotesAdapter
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.NotesMinimal
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.utilities.todayDate

class MainViewModel internal constructor(notesRepository: NotesRepository) : ViewModel() {

    val notesList = MediatorLiveData<List<NotesMinimal>>()
    var todaynote = MediatorLiveData<Notes>()
    val adapter: NotesAdapter
    var today: Boolean = false

    /**
     * Fill in the notesList & adapter
     */
    init {
        notesList.addSource(notesRepository.getallNotes(), notesList::setValue)
        todaynote.addSource(notesRepository.getNoteAtDate(todayDate())) {
            it?.let {
                today = true
                todaynote.value = it
            }
        }
        adapter = NotesAdapter()
    }
}
