package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.repository.NotesRepository
import kotlin.coroutines.CoroutineContext

class EditViewModel internal constructor(private val notesRepository: NotesRepository, private val id: Long) :
        ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    var note = MediatorLiveData<Notes>()

    /**
     * Fill in the note var
     */
    init {
        note.addSource(notesRepository.getThisNote(id), note::setValue)
    }

    fun updateNote(title: String, content: String) {
        val n = Notes(
                title = title,
                content = content,
                image = note.value!!.image,
                date = note.value!!.date,
                dateToSearch = note.value!!.dateToSearch,
                location = note.value!!.location
        ); n.id = note.value!!.id
        launch { notesRepository.update(n) }
    }

    fun deleteNote() {
        launch { notesRepository.deleteById(id) }
    }
}
