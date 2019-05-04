package me.martichou.be.grateful.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.martichou.be.grateful.R
import me.martichou.be.grateful.ui.add.AddViewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.system.exitProcess

class CompressImage(
        val context: Context,
        private val viewModel: AddViewModel?,
        val file: File,
        private val add_btn: AppCompatImageButton?
) {

    private val storageDir = context.getDir("imgForNotes", Context.MODE_PRIVATE)

    private lateinit var imageFile: File

    init {
        CoroutineScope(IO).launch {
            if (if (!storageDir.exists()) {
                        storageDir.mkdirs()
                    } else {
                        true
                    }
            ) {
                withContext(IO) { setup() }
                withContext(Default) { handling() }
            }
        }
    }

    private fun setup() {
        if (viewModel != null) {
            imageFile = File(storageDir, viewModel.randomImageName)
        }

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
        val fos = FileOutputStream(imageFile)
        try {
            BitmapFactory.decodeFile(file.absolutePath).compress(Bitmap.CompressFormat.JPEG, 75, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos.close()

                viewModel?.changeHasPhoto(true)
                viewModel?.changeIsWorking(false)

                withContext(Main) { add_btn?.background = ContextCompat.getDrawable(context, R.drawable.bg_roundaccent) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}