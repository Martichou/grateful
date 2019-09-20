package me.martichou.be.grateful.ui.settings

import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import me.martichou.be.grateful.util.notifications.NotificationHelper
import timber.log.Timber
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        mAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            hdl = this@SettingsFragment
        }

        setupSwitch()
        setupCheck()
        setupListener()
    }

    override fun onResume() {
        super.onResume()
        user = mAuth.currentUser
    }

    private fun setupSwitch() {
        binding.themedark.isChecked = sharedPreferences.getBoolean("themedark", false)
        binding.fullwidth.isChecked = sharedPreferences.getBoolean("fullwidth", false)
        binding.dailynotification.isChecked = sharedPreferences.getBoolean("dailynotification", true)
    }

    private fun setupCheck() {
        if (binding.dailynotification.isChecked) {
            binding.configureDaily.visibility = View.VISIBLE
            Timber.d("setupCheck Called")
            var minute: String = sharedPreferences.getInt("dn_min", 0).toString()
            if (minute.toInt() <= 9)
                minute = "0$minute"

            if (!NotificationHelper().checkIfExist(requireContext())) {
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
            if (isChecked) {
                // Enable black theme
                Aesthetic.config {
                    isDark(true)
                    textColorPrimary(res = R.color.white)
                    textColorSecondary(res = R.color.grey_lightlight)
                    textColorSecondaryInverse(res = R.color.grey_extraPlusLight)
                    colorPrimary(res = R.color.black)
                    colorAccent(res = R.color.red)
                    colorWindowBackground(res = R.color.black)
                    colorStatusBar(res = R.color.black)
                    colorNavigationBarAuto()
                    colorCardViewBackground(res = R.color.dark)
                    bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY_DARK)
                    bottomNavigationIconTextMode(BottomNavIconTextMode.BLACK_WHITE_AUTO)
                    swipeRefreshLayoutColorsRes(R.color.red)

                    snackbarBackgroundColor(res = R.color.white)
                    snackbarTextColor(res = android.R.color.black)
                }
            } else {
                // Disable black theme
                Aesthetic.config {
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

        binding.fullwidth.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("withdate $isChecked")
            sharedPreferences.edit().putBoolean("fullwidth", isChecked).apply()
        }

        binding.dailynotification.setOnCheckedChangeListener { _, isChecked ->
            Timber.d("dailynotification $isChecked")
            sharedPreferences.edit().putBoolean("dailynotification", isChecked).apply()
            if (isChecked) {
                // Enable notification
                if (NotificationHelper().checkIfExist(requireContext())) {
                    NotificationHelper().cancelAlarmRTC(requireContext())
                }
                NotificationHelper().scheduleRepeatingRTCNotification(requireContext(), 20, 0)
                NotificationHelper().enableBootReceiver(requireContext())
                setupCheck()
            } else {
                // Disable notification
                sharedPreferences.edit().remove("dn_hour").remove("dn_min").apply()
                if (NotificationHelper().checkIfExist(requireContext())) {
                    NotificationHelper().cancelAlarmRTC(requireContext())
                }
                NotificationHelper().disableBootReceiver(requireContext())
                setupCheck()
            }
        }
    }

    fun openDialog(v: View) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            sharedPreferences.edit().putInt("dn_hour", hourOfDay).putInt("dn_min", minute).apply()

            if (NotificationHelper().checkIfExist(requireContext())) {
                NotificationHelper().cancelAlarmRTC(requireContext())
            }

            setupCheck()
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(context)).show()
    }

    fun back(v: View) {
        findNavController().popBackStack()
    }

    fun gotoSync(v: View) {
        if (user == null) {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsNewFragmentToSettingsLogin())
        } else {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsNewFragmentToSettingsSync())
        }
    }

    fun gotoLegal(v: View) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/grateful-policy/accueil")));
        } catch (anfe: ActivityNotFoundException) {
            Toast.makeText(context, "There was an issue", Toast.LENGTH_SHORT).show()
        }
    }

}