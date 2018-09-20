package me.martichou.be.grateful.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.utilities.randomNumber

class EditViewModel internal constructor(
    private val notesRepository: NotesRepository,
    id: Long
) : ViewModel() {

    val randomImageName: String = randomNumber(100000000, 999999999)
    var hasPhotoUpdated: Boolean = false

    /**
     * Change the boolean value
     */
    fun changeHasPhotoUpdated(b: Boolean) {
        hasPhotoUpdated = b
    }

    /**
     * Get the note from the db
     */
    var note: LiveData<Notes> = notesRepository.getThisNote(id)

    /**
     * Get the static note
     */
    fun getThisNoteStatic(noteId: Long) = notesRepository.getThisNoteStatic(noteId)

    /**
     * Delete the note
     */
    fun deleteNote(note: Notes) = notesRepository.delete(note)

    /**
     * Return the photo name if there is one
     * else, blank
     */
    private fun photoOrNot(img: String): String {
        return if (hasPhotoUpdated) {
            randomImageName
        } else {
            img
        }
    }

    /**
     * Save the note on the db if it's needed
     * updateNote is on runOnIoThread
     */
    fun updateOnDb(pn: Notes, title: String, content: String, id: Long) {
        val n = Notes(title, content, photoOrNot(pn.image), pn.date)
        n.id = id

        notesRepository.update(n)
    }
}
