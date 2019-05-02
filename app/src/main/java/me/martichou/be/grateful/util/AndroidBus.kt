package me.martichou.be.grateful.util

import android.os.Handler
import android.os.Looper
import com.squareup.otto.Bus


class AndroidBus : Bus() {

    private val mainThread = Handler(Looper.getMainLooper())

    override fun post(event: Any) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event)
        } else {
            mainThread.post { post(event) }
        }
    }
}