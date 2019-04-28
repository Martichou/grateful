package me.martichou.be.grateful.view.ui

import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentSettingsBinding
import me.martichou.be.grateful.utils.notifications.NotificationHelper
import timber.log.Timber
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    // TODO Exclude arrow
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        mAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.hdl = this

        setupSwitch()
        setupCheck()
        setupListener()
    }

    override fun onResume() {
        user = mAuth.currentUser
        Timber.d("UID: ${mAuth.uid}")
        super.onResume()
    }

    private fun setupSwitch() {
        binding.themedark.isChecked = sharedPreferences.getBoolean("themedark", false)
        binding.dailynotification.isChecked = sharedPreferences.getBoolean("dailynotification", true)
    }

    private fun setupCheck(){
        if (binding.dailynotification!!.isChecked) {
            binding.configureDaily.visibility = View.VISIBLE
            Timber.d("setupCheck Called")
            var minute: String = sharedPreferences.getInt("dn_min", 0).toString()
            if(minute.toInt() <= 9)
                minute = "0$minute"

            if(!NotificationHelper().checkIfExist(requireContext())){
                NotificationHelper().scheduleRepeatingRTCNotification(requireContext(), sharedPreferences.getInt("dn_hour", 20), minute.toInt())
                NotificationHelper().enableBootReceiver(requireContext())
            } else {
                Timber.d("ZDZUQDBNQZBDIA")
            }
            binding.enableDailyStatus.text = resources.getString(R.string.scheduled_at, sharedPreferences.getInt("dn_hour", 20).toString(), minute)
        } else {
            binding.configureDaily.visibility = View.GONE
            binding.enableDailyStatus.text = resources.getString(R.string.daily_disabled)
        }
    }

    private fun setupListener() {
        binding.themedark.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("themedark $isChecked")
            sharedPreferences.edit().putBoolean("themedark", isChecked).apply()
            if(isChecked) {
                // Enable black theme
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
                // Disable black theme
                Aesthetic.config {
                    this.activityTheme(R.style.BaseAppTheme)
                    isDark(false)
                    textColorPrimary(res = R.color.almostblack)
                    textColorSecondary(res = R.color.grey_light)
                    textColorSecondaryInverse(res = R.color.grey_lightlight)
                    colorPrimary(res = R.color.white)
                    colorAccent(res = R.color.red)
                    colorWindowBackground(res = R.color.white)
                    colorStatusBar(res = R.color.white)
                    colorNavigationBarAuto()
                    colorCardViewBackground(res = R.color.fcfc)
                    bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
                    bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
                    swipeRefreshLayoutColorsRes(R.color.red)

                    snackbarBackgroundColorDefault()
                    snackbarTextColorDefault()
                }
            }
        }

        binding.dailynotification.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("dailynotification $isChecked")
            sharedPreferences.edit().putBoolean("dailynotification", isChecked).apply()
            if(isChecked) {
                // Enable notification
                if(NotificationHelper().checkIfExist(requireContext())) {
                    NotificationHelper().cancelAlarmRTC(requireContext())
                }
                NotificationHelper().scheduleRepeatingRTCNotification(requireContext(), 20, 0)
                NotificationHelper().enableBootReceiver(requireContext())
                setupCheck()
            } else {
                // Disable notification
                sharedPreferences.edit().remove("dn_hour").remove("dn_min").apply()
                if(NotificationHelper().checkIfExist(requireContext())) {
                    NotificationHelper().cancelAlarmRTC(requireContext())
                }
                NotificationHelper().disableBootReceiver(requireContext())
                setupCheck()
            }
        }
    }

    fun openDialog(v: View){
        val calendar = Calendar.getInstance()
        TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            sharedPreferences.edit().putInt("dn_hour", hourOfDay).putInt("dn_min", minute).apply()

            if(NotificationHelper().checkIfExist(requireContext())) {
                NotificationHelper().cancelAlarmRTC(requireContext())
            }

            setupCheck()
        }, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(context)).show()
    }

    fun back(v: View) {
        findNavController().popBackStack()
    }

    fun gotoSync(v: View) {
        if(user == null) {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsNewFragmentToSettingsLogin())
        } else {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsNewFragmentToSettingsSync())
        }
    }

    fun gotoLegal(v: View) {
        val browserIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent(Intent.ACTION_QUICK_VIEW, Uri.parse("https://sites.google.com/view/grateful-policy/accueil"))
        } else {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/grateful-policy/accueil"))
        }
        startActivity(browserIntent)
    }

}