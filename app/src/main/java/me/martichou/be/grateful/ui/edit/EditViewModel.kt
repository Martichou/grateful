package me.martichou.be.grateful.ui.edit

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.vo.Notes
import javax.inject.Inject

class EditViewModel
@Inject constructor(private val notesRepository: NotesRepository) : ViewModel() {

    var note = MediatorLiveData<Notes>()

    fun setNote(id: Long) {
        note.addSource(notesRepository.getThisNote(id), note::setValue)
    }

    fun updateNote(title: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val n = Notes(
                    title = title,
                    content = content,
                    image = note.value!!.image,
                    date = note.value!!.date,
                    dateToSearch = note.value!!.dateToSearch,
                    location = note.value!!.location
            ); n.id = note.value!!.id
            notesRepository.update(n)
        }
    }

    fun deleteNote() {
        notesRepository.deleteById(note.value!!.id)
    }

}
