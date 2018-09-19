package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.utilities.randomNumber

class AddViewModel internal constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    val randomImageName: String = randomNumber(100000000, 999999999)
    var isWorking: Boolean = false
    var hasPhoto: Boolean = false

    /**
     * Insert a new note in the db
     */
    fun insertNote(notes: Notes) = notesRepository.insert(notes)

    /**
     * Change the boolean value
     */
    fun changeIsWorking(b: Boolean) {
        isWorking = b
    }

    /**
     * Change the boolean value
     */
    fun changeHasPhoto(b: Boolean) {
        hasPhoto = b
    }
}
