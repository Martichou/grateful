package me.martichou.be.grateful.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.MultipleAnimationTestBinding
import me.martichou.be.grateful.utilities.CompressImage
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.utilities.makeToast
import me.martichou.be.grateful.viewmodels.ShowViewModel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import timber.log.Timber
import java.io.File

class TestActivity : AppCompatActivity(), MotionLayout.TransitionListener {

    private var noteId: Long = 0
    private val viewModel by lazy {
        getViewModel { ShowViewModel(getNotesRepository(this), noteId) }
    }
    private lateinit var binding: MultipleAnimationTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = intent.getLongExtra("noteId", 0)
        binding =
            DataBindingUtil.setContentView<MultipleAnimationTestBinding>(this, R.layout.multiple_animation_test).apply {
                setLifecycleOwner(this@TestActivity)
                showModel = viewModel
                hdl = this@TestActivity
            }

        binding.motionLayout.setTransitionListener(this)

        binding.titlenoteshow.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text = binding.titlenoteshow.text.toString()
                Timber.d(text)
                viewModel.updateTitle(text)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.notecontentshow.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text = binding.notecontentshow.text.toString()
                Timber.d(text)
                viewModel.updateContent(text)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        KeyboardVisibilityEvent.setEventListener(
            this
        ) {
            if (!it) {
                when (this.currentFocus) {
                    binding.titlenoteshow -> {
                        binding.titlenoteshow.clearFocus()
                    }
                    binding.notecontentshow -> {
                        binding.notecontentshow.clearFocus()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.titlenoteshow.text.toString().isEmpty()) {
            viewModel.updateTitle(viewModel.backedtitle)
            Timber.d(viewModel.backedtitle)
            makeToast(this, "You cannot use empty title.")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        Timber.d("Something changed")

        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show()
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

    fun deletethisnote(view: View) {
        viewModel.deleteNote(noteId)
        finish()
    }

    fun updateImage(view: View) {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(3, 4)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val image = File(CropImage.getActivityResult(data).uri.path)
                        CompressImage(this, null, viewModel, image, null)
                    }
                    CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                        makeToast(this, "We're sorry, there was an error.")
                    }
                }
            }
        }
    }
}