package me.martichou.be.grateful.utils.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import me.martichou.be.grateful.R
import me.martichou.be.grateful.view.ui.SplashScreen
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive true")
        val intentToRepeat = Intent(context, SplashScreen::class.java)
        intentToRepeat.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(context, 0, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT)

        NotificationHelper().getNotificationManager(context).notify(0, buildLocalNotification(context, pendingIntent).build())
    }

    private fun buildLocalNotification(context: Context, pendingIntent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "upload_channel_id").apply {
            setContentTitle("It's the perfect time to be grateful !")
            setContentIntent(pendingIntent)
            setSmallIcon(R.drawable.splashimage)
            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            setVibrate(null)
            setAutoCancel(true)
        }
    }
}