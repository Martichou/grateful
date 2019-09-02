package me.martichou.be.grateful.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import me.martichou.be.grateful.util.autoCleared
import me.martichou.be.grateful.util.notifications.NotificationHelper
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class HomeMainFragment : Fragment(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainViewModel
    private var binding by autoCleared<FragmentHomemainBinding>()
    private var adapter by autoCleared<NotesAdapter>()

    private var opening = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHomemainBinding.inflate(inflater, container, false).apply {
            // Setup toolbar menu item click listener
            // Don't know why setHasOptionMenu don't work
            toolbar.setOnMenuItemClickListener(this@HomeMainFragment)

            recentNotesList.setHasFixedSize(true)
            recentNotesList.addItemDecoration(DividerRV(requireContext()))
        }

        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move).apply {
            duration = 300
            interpolator = FastOutSlowInInterpolator()
        }

        // Wait RecyclerView layout for detail to list image return animation
        postponeEnterTransition()
        binding.recentNotesList.doOnLayout {
            startPostponedEnterTransition()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        // Set adapter to the recyclerview once other things are set
        adapter = NotesAdapter()

        binding.apply {
            thisVm = mainViewModel
            recentNotesList.adapter = adapter
            lifecycleOwner = viewLifecycleOwner
            hdl = this@HomeMainFragment
        }

        // Subscribe adapter
        subscribeUirecentNotesList(adapter)
    }

    override fun onResume() {
        super.onResume()
        opening = false
    }

    /**
     * Handle click on menu item
     */
    override fun onMenuItemClick(it: MenuItem): Boolean {
        when (it.itemId) {
            R.id.menu_main_today -> gototop()
            R.id.menu_main_setting -> openSettings()
        }
        return true
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
                binding.apply {
                    loadingUi.visibility = View.GONE
                    nonethinking.visibility = View.GONE
                }
            }
        })

        // Handle click on item list
        adapter.openNote.observe(viewLifecycleOwner, EventObserver { pair ->
            if (!opening) {
                opening = true
                val direction = HomeMainFragmentDirections.actionNoteListFragmentToNoteDetailFragment(pair.first.id)
                DataBindingUtil.getBinding<RecyclerviewHomeitemBinding>(pair.second)?.let {
                    val navigatorExtras = FragmentNavigatorExtras(it.showImageNote to pair.first.id.toString())
                    findNavController().navigate(direction, navigatorExtras)
                } ?: run {
                    findNavController().navigate(direction)
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
