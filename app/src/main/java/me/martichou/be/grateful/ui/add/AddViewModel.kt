package me.martichou.be.grateful.ui.add

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.vo.Notes
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.util.encryption.HashUtils
import me.martichou.be.grateful.util.dateDefault
import me.martichou.be.grateful.util.dateToSearch
import me.martichou.be.grateful.util.formatTodateToSearch
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.coroutines.CoroutineContext

class AddViewModel internal constructor(private val notesRepository: NotesRepository, context: Context) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private val storageDir = context.getDir("imgForNotes", Context.MODE_PRIVATE)
    var hasBeenSaved: Boolean = false
    var hasPhoto: Boolean = false

    val randomImageName: String = HashUtils.sha1(Date().toString())

    var isWorking: Boolean = false
    var placeCity: String? = null
    var dateSelected: Calendar? = null

    /**
     * Delete image created in app's memory as it's not saved
     */
    fun deleteImage() {
        launch {
            val imageFile = File(storageDir, randomImageName)
            if (imageFile.exists()) {
                val deleted = imageFile.delete()
                Timber.d("Deleting")
                if (!deleted) {
                    Timber.e("Cannot delete the file..")
                } else {
                    Timber.d("Succes. The file has been deleted")
                }
            }
        }
    }

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
     * Return the loc name if there is one else, blank
     */
    fun locOrNot(): String {
        return if (placeCity.isNullOrBlank()) {
            ""
        } else {
            placeCity.toString()
        }
    }

    /**
     * Return the timeInMillis
     */
    fun dateDefaultOrNot(): String {
        return if (dateSelected == null) {
            dateDefault()
        } else {
            dateSelected?.timeInMillis.toString()
        }
    }

    /**
     * Return the date chosen by the user or the current date if blank
     */
    fun dateOrNot(): String {
        return if (dateSelected == null) {
            dateToSearch()
        } else {
            formatTodateToSearch(dateSelected!!)
        }
    }
}
