package me.martichou.be.grateful.utilities.notifications

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import timber.log.Timber

class AlarmBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Timber.d("Boot Message Received")
            // TODO CHANGE HOUR
            NotificationHelper().scheduleRepeatingRTCNotification(context, "22", "42")
        }
    }
}