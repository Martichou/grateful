package me.martichou.be.grateful.view.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import me.martichou.be.grateful.R

class SplashScreen : AppCompatActivity() {

    private lateinit var mDelayHandler: Handler

    private val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)

            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDelayHandler = Handler()

        mDelayHandler.post(mRunnable)
    }

    override fun onPause() {
        super.onPause()

        mDelayHandler.removeCallbacks(mRunnable)
    }

    override fun onResume() {
        super.onResume()

        mDelayHandler.post(mRunnable)
    }

    public override fun onDestroy() {
        mDelayHandler.removeCallbacks(mRunnable)

        super.onDestroy()
    }
}