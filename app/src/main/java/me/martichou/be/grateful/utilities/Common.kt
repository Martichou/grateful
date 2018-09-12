package me.martichou.be.grateful.utilities

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

/**
 * @return the time with DAY MONTH N° YEARS
 */
fun currentTime(): String {

    val calendar = Calendar.getInstance()
    val date = calendar.time
    val time = date.time
    val currentdayofmonth = calendar.get(Calendar.DAY_OF_MONTH)
    val year = calendar.get(Calendar.YEAR)
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(time)
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(time)

    return "$dayName, $monthName $currentdayofmonth, $year"
}

/**
 * @return a random number between 10000000 & 99999999
 */
fun randomNumber(): String {
    val v = runOnIoThread { Random().nextInt(99999999 - 10000000 + 1 + 10000000) }
    return v.toString()
}