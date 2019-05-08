package me.martichou.be.grateful

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    private lateinit var mDelayHandler: Handler

    private val mRunnable = {
        if (!isFinishing) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out)

            mDelayHandler.postDelayed({ finish() }, 150)
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