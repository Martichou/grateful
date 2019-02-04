package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.data.repository.NotesRepository
import me.martichou.be.grateful.utilities.randomNumber
import kotlin.coroutines.CoroutineContext

class AddViewModel internal constructor(private val notesRepository: NotesRepository) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    var hasPhoto: Boolean = false
    val randomImageName: String = randomNumber(100000000, 999999999)
    var isWorking: Boolean = false
    var placeCity: String? = null

    /**
     * Insert a new note in the db
     */
    fun insertNote(notes: Notes) = launch { notesRepository.insert(notes) }

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
     * Return the photo name if there is one else, blank
     */
    fun locOrNot(): String {
        return if (placeCity.isNullOrBlank()) {
            ""
        } else {
            placeCity.toString()
        }
    }
}
