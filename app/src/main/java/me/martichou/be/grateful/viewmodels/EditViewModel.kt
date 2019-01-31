package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.repository.NotesRepository

class EditViewModel internal constructor(private val notesRepository: NotesRepository, private val id: Long) :
    ViewModel() {

    var note = MediatorLiveData<Notes>()

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()
    /**
     * This is the scope for all coroutines launched by [ShowViewModel].
     *
     * Since we pass [viewModelJob], you can cancel all coroutines launched by [viewModelScope] by calling
     * viewModelJob.cancel().  This is called in [onCleared].
     */
    private val viewModelScope = CoroutineScope(Main + viewModelJob)

    /**
     * Cancel all coroutines when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Fill in the note var
     */
    init {
        note.addSource(notesRepository.getThisNote(id), note::setValue)
    }

    fun deleteNote() = viewModelScope.launch { notesRepository.deleteById(id) }
}
