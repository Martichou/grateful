package me.martichou.be.grateful.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.MultipleAnimationTestBinding
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.viewmodels.ShowViewModel

class TestActivity : AppCompatActivity(), MotionLayout.TransitionListener {

    private var noteId: Long = 0
    private val viewModel by lazy {
        getViewModel { ShowViewModel(getNotesRepository(this), noteId) }
    }
    private lateinit var mL: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = intent.getLongExtra("noteId", 0)
        DataBindingUtil.setContentView<MultipleAnimationTestBinding>(this, R.layout.multiple_animation_test).apply {
            setLifecycleOwner(this@TestActivity)
            showModel = viewModel
            hdl = this@TestActivity
            mL = motionLayout
        }
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

    fun deletethisnote(view: View){
        viewModel.deleteNote(noteId)
        finish()
    }

}