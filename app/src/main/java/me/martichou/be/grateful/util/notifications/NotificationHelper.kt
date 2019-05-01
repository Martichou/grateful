package me.martichou.be.grateful.util.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import timber.log.Timber
import java.util.*

class NotificationHelper {

    var alarmManagerRTC: AlarmManager? = null
    var alarmIntentRTC: PendingIntent? = null

    /**
     * This is the real time /wall clock time
     * @param context
     */
    fun scheduleRepeatingRTCNotification(context: Context, hour: Int, min: Int) {
        val calendar = Calendar.getInstance()
        val currHr = calendar.get(Calendar.HOUR_OF_DAY)
        val currMin = calendar.get(Calendar.MINUTE)
        calendar.timeInMillis = System.currentTimeMillis()
        Timber.d("Current hour $currHr")
        if(currHr > hour){
            Timber.d("Date added")
            calendar.add(Calendar.DATE, 1)
        } else if (currHr == hour && Math.abs(min-currMin) >= 30) {
            Timber.d("Date added")
            calendar.add(Calendar.DATE, 1)
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, min)
        calendar.set(Calendar.SECOND, 0)

        val intent = Intent(context, AlarmReceiver::class.java)
        alarmIntentRTC = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManagerRTC = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManagerRTC!!.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntentRTC)
        Timber.d("scheduleRepeatingRTCNotification enabled")
    }

    fun cancelAlarmRTC(context: Context) {
        alarmIntentRTC?.cancel()
        alarmManagerRTC?.cancel(alarmIntentRTC)
        Timber.d("Checking if disabled correctly...")
        val alarmUp = PendingIntent.getBroadcast(context, 0, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
        Timber.d("Is it still up? ${alarmUp != null}")
    }

    fun checkIfExist(context: Context): Boolean {
        val alarmUp = PendingIntent.getBroadcast(context, 0, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)

        return if (alarmUp != null) {
            alarmUp.cancel()
            Timber.d("Alarm is already active")
            true
        } else {
            false
        }
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