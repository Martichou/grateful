package me.martichou.be.grateful.util

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class SaveImage(
        val context: Context,
        val file: Bitmap,
        private val imageName: String
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    private val storageDir = context.getDir("imgForNotes", Context.MODE_PRIVATE)

    private lateinit var imageFile: File

    init {
        launch {
            if (if (!storageDir.exists()) {
                        storageDir.mkdirs()
                    } else {
                        true
                    }
            ) {
                withContext(Dispatchers.IO) { setup() }
                withContext(Dispatchers.Default) { handling() }
            }
        }
    }

    private fun setup() {
        imageFile = File(storageDir, imageName)

        if (imageFile.exists()) {
            val deleted = imageFile.delete()
            Timber.d("Deleting")
            if (!deleted) {
                Timber.e("Cannot delete the file..")
                exitProcess(-1)
            }
        }
    }

    private suspend fun handling() {
        val fos: FileOutputStream = withContext(Dispatchers.Default) { FileOutputStream(imageFile) }
        try {
            withContext(Dispatchers.Default) {
                file.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                withContext(Dispatchers.Default) { fos.close() }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}