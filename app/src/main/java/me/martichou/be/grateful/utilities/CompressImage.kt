package me.martichou.be.grateful.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.martichou.be.grateful.R
import me.martichou.be.grateful.viewmodels.AddViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class CompressImage(context: Context, viewModel: AddViewModel, file: File, add_btn: AppCompatImageButton) : CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext = job + Main

    private val storageDir = context.getDir("imgForNotes", Context.MODE_PRIVATE)

    init {
        launch {
            if (if (!storageDir.exists()) { storageDir.mkdirs() } else { true }) {
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
                    async(IO) {
                        BitmapFactory.decodeFile(file.absolutePath).compress(Bitmap.CompressFormat.JPEG, 70, fos)
                    }.await()
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