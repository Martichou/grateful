package me.martichou.be.grateful.utils.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager

class AlarmBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dailynotification", false)) {
                NotificationHelper().scheduleRepeatingRTCNotification(context,
                        PreferenceManager.getDefaultSharedPreferences(context).getInt("dn_hour", 20),
                        PreferenceManager.getDefaultSharedPreferences(context).getInt("dn_min", 0))
            }
        }
    }
}