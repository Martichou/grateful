package me.martichou.be.grateful.utilities.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import me.martichou.be.grateful.R
import me.martichou.be.grateful.activities.SplashScreen

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //Get notification manager to manage/send notifications
        //Intent to invoke app when click on notification.
        //In this sample, we want to start/launch this sample app when user clicks on notification
        val intentToRepeat = Intent(context, SplashScreen::class.java)
        //set flag to restart/relaunch the app
        intentToRepeat.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        //Pending intent to handle launch of Activity in intent above
        val pendingIntent = PendingIntent.getActivity(context, NotificationHelper.ALARM.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT)

        //Build notification
        val repeatedNotification = buildLocalNotification(context, pendingIntent).build()

        //Send local notification
        NotificationHelper().getNotificationManager(context).notify(NotificationHelper.ALARM.ALARM_TYPE_RTC, repeatedNotification)
    }

    private fun buildLocalNotification(context: Context, pendingIntent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "upload_channel_id").apply {
            setContentTitle("It's the perfect time to be grateful !")
            setContentIntent(pendingIntent)
            setSmallIcon(R.mipmap.ic_launcher_round)
            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            setVibrate(null)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
    }
}