package me.martichou.be.grateful.ui.home

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.vo.Notes

class MainViewModel internal constructor(notesRepository: NotesRepository) : ViewModel() {

    val recentNotesList = MediatorLiveData<List<Notes>>().apply {
        addSource(notesRepository.getAllNote(), this::setValue)
    }
    val adapter = NotesAdapter()

}
