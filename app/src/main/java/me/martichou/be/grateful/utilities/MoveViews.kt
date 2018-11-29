package me.martichou.be.grateful.utilities

import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.TransitionSet

class MoveViews : TransitionSet() {

    init {
        init()
    }

    private fun init() {
        addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
    }
}