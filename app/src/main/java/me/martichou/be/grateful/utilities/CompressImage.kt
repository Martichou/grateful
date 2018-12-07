package me.martichou.be.grateful.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import me.martichou.be.grateful.R
import me.martichou.be.grateful.viewmodels.AddViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.system.exitProcess

class CompressImage(context: Context, viewModel: AddViewModel, file: File, add_btn: AppCompatImageButton) {

    private val job = SupervisorJob()

    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val storageDir = context.getDir("imgForNotes", Context.MODE_PRIVATE)

    init {
        scope.launch {
            if (if (!storageDir.exists()) {
                        storageDir.mkdirs()
                    } else {
                        true
                    }) {
                val imageFile = File(storageDir, viewModel.randomImageName)
                if (imageFile.exists()) {
                    val deleted = imageFile.delete()
                    if (!deleted) {
                        Log.i("Error", "Cannot delete the file..")
                        exitProcess(-1)
                    }
                }
                val fos: FileOutputStream = async(IO) { FileOutputStream(imageFile) }.await()
                try {
                    async(IO) { BitmapFactory.decodeFile(file.absolutePath).compress(Bitmap.CompressFormat.JPEG, 75, fos) }.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        async(IO) { fos.close() }.await()
                        viewModel.changeHasPhoto(true)
                        viewModel.changeIsWorking(false)

                        add_btn.background = ContextCompat.getDrawable(context, R.drawable.bg_roundaccent)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}