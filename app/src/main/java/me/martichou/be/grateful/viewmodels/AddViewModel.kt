package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.utilities.randomNumber

class AddViewModel internal constructor(private val notesRepository: NotesRepository) : ViewModel() {

    val randomImageName: String = randomNumber(100000000, 999999999)
    var isWorking: Boolean = false
    var hasPhoto: Boolean = false
    var place: String? = null

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

    /**
     * Return the photo name if there is one
     * else, blank
     */
    fun photoOrNot(): String {
        return if (hasPhoto) {
            randomImageName
        } else {
            ""
        }
    }

    /**
     * Return the photo name if there is one
     * else, blank
     */
    fun locOrNot(): String {
        return if (place.isNullOrBlank()) {
            ""
        } else {
            place.toString()
        }
    }
}
