package me.martichou.be.grateful.ui.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.vo.Notes

class ShowViewModel internal constructor(notesRepository: NotesRepository, id: Long) : ViewModel() {

    var note = MediatorLiveData<Notes>().apply { addSource(notesRepository.getThisNote(id), this::setValue) }

}
