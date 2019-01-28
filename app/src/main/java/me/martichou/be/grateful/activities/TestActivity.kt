package me.martichou.be.grateful.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import me.martichou.be.grateful.R

class TestActivity : AppCompatActivity(), MotionLayout.TransitionListener {

    private var layoutId = 0

    private val motionLayout by lazy {
        findViewById<MotionLayout>(R.id.motionLayout)
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutId = R.layout.multiple_animation_test
        setContentView(layoutId)
        motionLayout.setTransitionListener(this)
    }
}