package me.martichou.be.grateful.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import me.martichou.be.grateful.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey)

        val darkSwitch = findPreference<SwitchPreferenceCompat>("themedark")
        darkSwitch?.setOnPreferenceChangeListener { _, newValue ->
            if ((newValue as Boolean)) {
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

        val feedbackButton = findPreference<Preference>("feedback")
        feedbackButton?.setOnPreferenceClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:martichou.andre@gmail.com")
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Grateful Feedback")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "What do you think about the app?")

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email using..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show()
            }
            true
        }

        val aboutButton = findPreference<Preference>("about")
        aboutButton?.setOnPreferenceClickListener {
           // TODO
            true
        }
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