package me.martichou.be.grateful.ui.edit

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.repository.NotesRepository
import me.martichou.be.grateful.vo.Notes
import timber.log.Timber
import java.io.File
import kotlin.coroutines.CoroutineContext

class EditViewModel internal constructor(private val notesRepository: NotesRepository, private val id: Long, context: Context) :
        ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private val storageDir = context.getDir("imgForNotes", Context.MODE_PRIVATE)
    var note = MediatorLiveData<Notes>().apply { addSource(notesRepository.getThisNote(id), this::setValue) }

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
        launch {
            val imageFile = File(storageDir, note.value?.image)
            if (imageFile.exists()) {
                val deleted = imageFile.delete()
                Timber.d("Deleting")
                if (!deleted) {
                    Timber.e("Cannot delete the file..")
                } else {
                    Timber.d("Succes. The file has been deleted")
                }
            }

            notesRepository.deleteById(id)
        }
    }

}
