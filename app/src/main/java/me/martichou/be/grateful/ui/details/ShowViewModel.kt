package me.martichou.be.grateful.ui.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.vo.Notes
import javax.inject.Inject

class ShowViewModel
@Inject constructor(private val notesRepository: NotesRepository) : ViewModel() {

    var note = MediatorLiveData<Notes>()

    fun setNote(id: Long){
        note.addSource(notesRepository.getThisNote(id), note::setValue)
    }
}
