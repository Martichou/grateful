package me.martichou.be.grateful.view.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.wooplr.spotlight.SpotlightView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.databinding.RecyclerviewHomeitemBinding
import me.martichou.be.grateful.utils.DividerRV
import me.martichou.be.grateful.utils.EventObserver
import me.martichou.be.grateful.utils.ToolbarElevationOffsetListener
import me.martichou.be.grateful.utils.notifications.NotificationHelper
import me.martichou.be.grateful.view.adapter.NotesAdapter
import me.martichou.be.grateful.viewmodel.MainViewModel
import me.martichou.be.grateful.viewmodel.getNotesRepository
import me.martichou.be.grateful.viewmodel.getViewModel
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class HomeMainFragment : Fragment(), CoroutineScope, androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private val viewModel by lazy {
        requireActivity().getViewModel { MainViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: FragmentHomemainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomemainBinding.inflate(inflater, container, false)

        // Wait for recyclerview
        postponeEnterTransition()

        // Set animation transition w/ sharedelement
        setupTransition()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.thisVm = viewModel
        binding.hdl = this

        // Prepare recyclerview and bind
        binding.recentNotesList.setHasFixedSize(true)
        binding.recentNotesList.addItemDecoration(DividerRV(requireContext()))

        // Set adapter to the recyclerview once other things are set
        binding.recentNotesList.adapter = viewModel.adapter

        // Wait RecyclerView layout for detail to list image return animation
        binding.recentNotesList.doOnLayout {
            startPostponedEnterTransition()
        }

        // Check notification
        checkForNotification()

        // Subscribe adapter
        subscribeUirecentNotesList(viewModel.adapter)

        // Update subtitle while scrolling
        setupScrollRvListener()

        // Setup toolbar menu item click listener
        // Don't know why setHasOptionMenu don't work
        binding.toolbar.setOnMenuItemClickListener(this)
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
            if(NotificationHelper().alarmIntentRTC != null) {
                NotificationHelper().cancelAlarmRTC()
            }
            NotificationHelper().scheduleRepeatingRTCNotification(requireContext(),
                    PreferenceManager.getDefaultSharedPreferences(context).getInt("dn_hour", 20),
                    PreferenceManager.getDefaultSharedPreferences(context).getInt("dn_min", 0))
            NotificationHelper().enableBootReceiver(requireContext())

            Timber.d("Notification enabled")
        }
    }

    /**
     * Observe recentNotesList for and update adapter
     */
    private fun subscribeUirecentNotesList(adapter: NotesAdapter) {
        // Fill adapter item list
        viewModel.recentNotesList.observe(viewLifecycleOwner, Observer { notes ->
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
     * Setup sharedelementransition
     */
    private fun setupTransition() {
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move).apply { duration = 250 }
    }

    /**
     * Hide fab when scrolled
     */
    private fun setupScrollRvListener() {
        binding.appBar.addOnOffsetChangedListener(ToolbarElevationOffsetListener(binding))

        binding.recentNotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0 && binding.fab.visibility == View.VISIBLE) {
                    binding.fab.hide()
                } else if (dy < 0 && binding.fab.visibility != View.VISIBLE) {
                    binding.fab.show()
                }
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
