package me.martichou.be.grateful.view.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import me.martichou.be.grateful.R
import me.martichou.be.grateful.utils.notifications.NotificationHelper
import timber.log.Timber
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey)

        val dailynotification = findPreference<SwitchPreferenceCompat>("dailynotification")
        val defineTime = findPreference<Preference>("defineTime")
        val feedbackButton = findPreference<Preference>("feedback")
        val darkSwitch = findPreference<SwitchPreferenceCompat>("themedark")
        val aboutButton = findPreference<Preference>("about")
        val sync = findPreference<Preference>("sync")

        darkSwitch?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                Aesthetic.config {
                    this.activityTheme(R.style.BaseAppThemeDark)
                    isDark(true)
                    textColorPrimary(res = R.color.white)
                    textColorSecondary(res = R.color.grey_lightlight)
                    textColorSecondaryInverse(res = R.color.grey_extraPlusLight)
                    colorPrimary(res = R.color.black)
                    colorAccent(res = R.color.red)
                    colorWindowBackground(res = R.color.black)
                    colorStatusBar(res = R.color.black)
                    colorNavigationBarAuto()
                    bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
                    bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
                    swipeRefreshLayoutColorsRes(R.color.red)

                    snackbarBackgroundColor(res = R.color.white)
                    snackbarTextColor(res = android.R.color.black)
                }
            } else {
                Aesthetic.config {
                    this.activityTheme(R.style.BaseAppTheme)
                    isDark(false)
                    textColorPrimary(res = R.color.black)
                    textColorSecondary(res = R.color.grey_light)
                    textColorSecondaryInverse(res = R.color.grey_lightlight)
                    colorPrimary(res = R.color.white)
                    colorAccent(res = R.color.red)
                    colorWindowBackground(res = R.color.white)
                    colorStatusBar(res = R.color.white)
                    colorNavigationBarAuto()
                    textColorPrimary(Color.BLACK)
                    bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
                    bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
                    swipeRefreshLayoutColorsRes(R.color.red)

                    snackbarBackgroundColorDefault()
                    snackbarTextColorDefault()
                }
            }
            true
        }

        if (dailynotification!!.isChecked) {
            var minute: String = preferenceManager.sharedPreferences.getInt("dn_min", 0).toString()
            if(minute.toInt() <= 9)
                minute = "0$minute"

            defineTime?.summary = resources.getString(R.string.scheduled_at, preferenceManager.sharedPreferences.getInt("dn_hour", 20).toString(), minute)
        } else {
            defineTime?.summary = resources.getString(R.string.daily_disabled)
        }

        dailynotification.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean){
                NotificationHelper().enableBootReceiver(requireContext())
                defineTime?.summary = resources.getString(R.string.scheduled_at, "20", "00")
            } else {
                preferenceManager.sharedPreferences.edit().remove("dn_hour").remove("dn_min").apply()
                NotificationHelper().cancelAlarmRTC()
                NotificationHelper().disableBootReceiver(requireContext())
                defineTime?.summary = resources.getString(R.string.daily_disabled)
            }
            true
        }

        defineTime?.setOnPreferenceClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                preferenceManager.sharedPreferences.edit().putInt("dn_hour", hourOfDay).putInt("dn_min", minute).apply()

                NotificationHelper().cancelAlarmRTC()
                NotificationHelper().scheduleRepeatingRTCNotification(requireContext(), hourOfDay, minute)
                var min = minute.toString()
                if(minute <= 9)
                    min = "0$minute"

                defineTime.summary = resources.getString(R.string.scheduled_at, hourOfDay.toString(), min)
            }, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),DateFormat.is24HourFormat(context)).show()
            true
        }

        feedbackButton?.setOnPreferenceClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:martichou.andre@gmail.com")
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Grateful Feedback")
            emailIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.what_do_you_think))

            try {
                startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.send_with)))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(context, resources.getString(R.string.no_email_client), Toast.LENGTH_SHORT).show()
            }
            true
        }

        aboutButton?.setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/grateful-policy/accueil"))
            startActivity(browserIntent)
            true
        }

        sync?.setOnPreferenceClickListener {
            if (user == null) {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSettingsSyncPhone())
            } else {
               findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSettingsSyncDataReal())
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser
        Timber.d("UID: ${mAuth.uid}")
    }

    private fun setAllPreferencesToAvoidHavingExtraSpace(preference: Preference) {
        preference.isIconSpaceReserved = false
        if (preference is PreferenceGroup)
            for (i in 0 until preference.preferenceCount)
                setAllPreferencesToAvoidHavingExtraSpace(preference.getPreference(i))
    }

    override fun setPreferenceScreen(preferenceScreen: PreferenceScreen?) {
        if (preferenceScreen != null)
            setAllPreferencesToAvoidHavingExtraSpace(preferenceScreen)
        super.setPreferenceScreen(preferenceScreen)

    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> =
            object : PreferenceGroupAdapter(preferenceScreen) {
                @SuppressLint("RestrictedApi")
                override fun onPreferenceHierarchyChange(preference: Preference?) {
                    if (preference != null)
                        setAllPreferencesToAvoidHavingExtraSpace(preference)
                    super.onPreferenceHierarchyChange(preference)
                }
            }

}