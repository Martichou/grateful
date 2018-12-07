package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.utilities.randomNumber

class AddViewModel internal constructor(private val notesRepository: NotesRepository) : ViewModel() {

    val randomImageName: String = randomNumber(100000000, 999999999)
    var isWorking: Boolean = false
    private var hasPhoto: Boolean = false
    var placeCity: String? = null
    var formatedDate: String? = null

    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()
    /**
     * This is the scope for all coroutines launched by [AddViewModel].
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
     * Insert a new note in the db
     */
    fun insertNote(notes: Notes) = viewModelScope.launch { notesRepository.insert(notes) }

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
        return if (placeCity.isNullOrBlank()) {
            ""
        } else {
            placeCity.toString()
        }
    }
}
