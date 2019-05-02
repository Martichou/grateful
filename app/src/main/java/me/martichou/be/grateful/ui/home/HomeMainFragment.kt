package me.martichou.be.grateful.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.wooplr.spotlight.SpotlightView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.databinding.RecyclerviewHomeitemBinding
import me.martichou.be.grateful.di.Injectable
import me.martichou.be.grateful.ui.add.AddMainFragment
import me.martichou.be.grateful.util.DividerRV
import me.martichou.be.grateful.util.EventObserver
import me.martichou.be.grateful.util.ToolbarElevationOffsetListener
import me.martichou.be.grateful.util.notifications.NotificationHelper
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class HomeMainFragment : Fragment(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentHomemainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomemainBinding.inflate(inflater, container, false)

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move).apply {
            duration = 400
            interpolator = FastOutSlowInInterpolator()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.thisVm = mainViewModel
        binding.hdl = this

        // Prepare recyclerview and bind
        binding.recentNotesList.setHasFixedSize(true)
        binding.recentNotesList.addItemDecoration(DividerRV(requireContext()))

        // Set adapter to the recyclerview once other things are set
        val adapter = mainViewModel.adapter
        binding.recentNotesList.adapter = mainViewModel.adapter

        // Wait RecyclerView layout for detail to list image return animation
        postponeEnterTransition()
        binding.recentNotesList.viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
        }

        // Setup toolbar menu item click listener
        // Don't know why setHasOptionMenu don't work
        binding.toolbar.setOnMenuItemClickListener(this)

        // Check notification
        checkForNotification()

        // Subscribe adapter
        subscribeUirecentNotesList(adapter)
    }

    /**
     * Handle click on menu item
     */
    override fun onMenuItemClick(it: MenuItem): Boolean {
        Timber.d("Clicked")
        when (it.itemId) {
            R.id.menu_main_today -> gototop()
            R.id.menu_main_setting -> openSettings()
        }
        return true
    }

    /**
     * Check if it's needed to enable notification
     */
    private fun checkForNotification() {
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dailynotification", true)) {
            if(!NotificationHelper().checkIfExist(requireContext())) {
                NotificationHelper().scheduleRepeatingRTCNotification(requireContext(),
                        PreferenceManager.getDefaultSharedPreferences(context).getInt("dn_hour", 20),
                        PreferenceManager.getDefaultSharedPreferences(context).getInt("dn_min", 0))
                NotificationHelper().enableBootReceiver(requireContext())
            }
        }
    }

    /**
     * Observe recentNotesList for and update adapter
     */
    private fun subscribeUirecentNotesList(adapter: NotesAdapter) {
        // Fill adapter item list
        mainViewModel.recentNotesList.observe(viewLifecycleOwner, Observer { notes ->
            if (notes.isNullOrEmpty()) {
                adapter.submitList(null)
                binding.loadingUi.visibility = View.GONE
                binding.nonethinking.visibility = View.VISIBLE

                SpotlightView.Builder(requireActivity())
                    .introAnimationDuration(400)
                    .enableRevealAnimation(true)
                    .performClick(true)
                    .fadeinTextDuration(400)
                    .headingTvColor(Color.parseColor("#FF6575"))
                    .headingTvSize(32)
                    .headingTvText(resources.getString(R.string.welcome))
                    .subHeadingTvColor(Color.parseColor("#ffffff"))
                    .subHeadingTvSize(16)
                    .subHeadingTvText(resources.getString(R.string.noneyet))
                    .maskColor(Color.parseColor("#dc000000"))
                    .target(binding.fab)
                    .lineAnimDuration(350)
                    .lineAndArcColor(Color.parseColor("#FF6575"))
                    .dismissOnTouch(true)
                    .dismissOnBackPress(true)
                    .enableDismissAfterShown(true)
                    .usageId("sp_fab")
                    .show()
            } else {
                adapter.submitList(notes)
                binding.loadingUi.visibility = View.GONE
                binding.nonethinking.visibility = View.GONE
            }
        })

        // Handle click on item list
        adapter.openNote.observe(viewLifecycleOwner, EventObserver { pair ->
            val direction = HomeMainFragmentDirections.actionNoteListFragmentToNoteDetailFragment(pair.first.id)
            DataBindingUtil.getBinding<RecyclerviewHomeitemBinding>(pair.second)?.let {
                val navigatorExtras = FragmentNavigatorExtras(it.showImageNote to pair.first.id.toString())
                findNavController().navigate(direction, navigatorExtras)
            } ?: run {
                findNavController().navigate(direction)
            }
        })
    }

    /**
     * Action button
     */
    fun btnNewAction(view: View) {
        val bottomsheetFragment = AddMainFragment()
        bottomsheetFragment.show(requireFragmentManager(), bottomsheetFragment.tag)
    }

    /**
     * Scroll to top of the list (today)
     */
    private fun gototop() {
        val item = (binding.recentNotesList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        Timber.d("Item $item")
        when (item) {
            0 -> Snackbar.make(binding.root, resources.getString(R.string.already_today), Snackbar.LENGTH_SHORT).show()
            -1 -> Snackbar.make(binding.root, resources.getString(R.string.add_first), Snackbar.LENGTH_SHORT).show()
            else -> binding.recentNotesList.smoothScrollToPosition(0)
        }
    }

    /**
     * Open settings
     */
    private fun openSettings() {
        findNavController().navigate(HomeMainFragmentDirections.actionMainFragmentToSettingsNewFragment())
    }
}
