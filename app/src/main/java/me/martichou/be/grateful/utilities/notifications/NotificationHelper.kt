package me.martichou.be.grateful.utilities.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*
import android.content.Context.ALARM_SERVICE
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.content.ComponentName
import android.os.Build
import timber.log.Timber

class NotificationHelper {

    object ALARM {
        var ALARM_TYPE_RTC = 100
    }

    private var alarmManagerRTC: AlarmManager? = null
    private var alarmIntentRTC: PendingIntent? = null

    /**
     * This is the real time /wall clock time
     * @param context
     */
    fun scheduleRepeatingRTCNotification(context: Context, hour: String, min: String) {
        // Get calendar instance to be able to select what time notification should be scheduled
        val calendar = Calendar.getInstance()
        // Setting time of the day (8am here) when notification will be sent every day (default)
        calendar.set(Calendar.HOUR_OF_DAY, hour.toInt(), min.toInt())

        // Setting intent to class where Alarm broadcast message will be handled
        val intent = Intent(context, AlarmReceiver::class.java)
        // Setting alarm pending intent
        alarmIntentRTC = PendingIntent.getBroadcast(context, ALARM.ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Getting instance of AlarmManager service
        alarmManagerRTC = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // Setting alarm to wake up device every day for clock time.
        // AlarmManager.RTC_WAKEUP is responsible to wake up device for sure, which may not be good practice all the time.
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> alarmManagerRTC!!.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmManager.INTERVAL_DAY, alarmIntentRTC)
            Build.VERSION.SDK_INT in 19..22 -> alarmManagerRTC!!.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, alarmIntentRTC)
            else -> alarmManagerRTC!!.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntentRTC)
        }
    }

    fun cancelAlarmRTC() {
        alarmManagerRTC?.cancel(alarmIntentRTC)
    }

    fun getNotificationManager(context: Context): NotificationManager {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "upload_channel_id"
        val channelName = "Upload"
        var importance = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_DEFAULT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        return notificationManager
    }

    /**
     * Enable boot receiver to persist alarms set for notifications across device reboots
     */
    fun enableBootReceiver(context: Context) {
        val receiver = ComponentName(context, AlarmBootReceiver::class.java)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }

    /**
     * Disable boot receiver when user cancels/opt-out from notifications
     */
    fun disableBootReceiver(context: Context) {
        val receiver = ComponentName(context, AlarmBootReceiver::class.java)
        val pm = context.packageManager

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

}