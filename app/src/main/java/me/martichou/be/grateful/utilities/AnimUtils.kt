package me.martichou.be.grateful.utilities

import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutLinearInInterpolator

/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

object AnimUtils {

    private val fastOutLinearIn by lazy { FastOutLinearInInterpolator() }

    /**
     * Incoming elements are animated using deceleration easing, which starts a transition
     * at peak velocity (the fastest point of an element’s movement) and ends at rest.
     *
     * Suitable timing for animating Views entering a screen
     *
     * See <a href="https://material.io/design/motion/speed.html#easing">
     *     https://material.io/design/motion/speed.html#easing</a>
     */
    fun getFastOutLinearInInterpolator(): Interpolator? {
        return fastOutLinearIn
    }

}