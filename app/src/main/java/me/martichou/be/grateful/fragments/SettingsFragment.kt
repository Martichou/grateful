package me.martichou.be.grateful.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.R
import android.widget.Toast
import android.content.Intent
import android.net.Uri


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey)

        val feedbackButton = findPreference<Preference>("feedback")
        feedbackButton.setOnPreferenceClickListener {
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