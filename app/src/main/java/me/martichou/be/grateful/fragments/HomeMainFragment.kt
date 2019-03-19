package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.databinding.RecyclerviewHomeitemBinding
import me.martichou.be.grateful.recyclerView.EventObserver
import me.martichou.be.grateful.recyclerView.NotesAdapter
import me.martichou.be.grateful.utilities.formatDate
import me.martichou.be.grateful.viewmodels.MainViewModel
import me.martichou.be.grateful.viewmodels.getNotesRepository
import me.martichou.be.grateful.viewmodels.getViewModel
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
    private var liststate: Parcelable? = null

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

        // Set adapter to the recyclerview once other things are set
        binding.recentNotesList.adapter = viewModel.adapter

        // Wait RecyclerView layout for detail to list image return animation
        binding.recentNotesList.doOnLayout {
            startPostponedEnterTransition()
        }

        // Subscribe adapter
        subscribeUirecentNotesList(viewModel.adapter)

        // Update subtitle while scrolling
        setupScrollRvListener()

        // Setup toolbar menu item click listener
        // Don't know why setHasOptionMenu don't work
        binding.toolbar.setOnMenuItemClickListener(this)

    }

    /**
     * Set the status bar as white
     */
    override fun onResume() {
        super.onResume()
        if (liststate != null) {
            binding.recentNotesList.layoutManager?.onRestoreInstanceState(liststate)
        }
    }


    /**
     * Save scrolled position
     */
    override fun onPause() {
        super.onPause()
        liststate = binding.recentNotesList.layoutManager?.onSaveInstanceState()
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
     * Observe recentNotesList for and update adapter
     */
    private fun subscribeUirecentNotesList(adapter: NotesAdapter) {
        viewModel.recentNotesList.observe(viewLifecycleOwner, Observer { notes ->
            if (notes.isNullOrEmpty()) {
                adapter.submitList(null)
                binding.loadingUi.visibility = View.GONE
                binding.nonethinking.visibility = View.VISIBLE
            } else {
                adapter.submitList(notes)
                binding.loadingUi.visibility = View.GONE
                binding.nonethinking.visibility = View.GONE
            }
        })

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
     * Setup fade out transition and sharedelementransition
     */
    private fun setupTransition() {
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move).apply { duration = 250 }
    }

    /**
     * Change date in the toolbar
     * But also hide fab when scrolled
     */
    private fun setupScrollRvListener() {
        binding.recentNotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0 && binding.fab.visibility == View.VISIBLE) {
                    binding.fab.hide()
                } else if (dy < 0 && binding.fab.visibility != View.VISIBLE) {
                    binding.fab.show()
                }

                val itemPosition = (binding.recentNotesList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                if (itemPosition != -1)
                    getDateFromItem(itemPosition)
            }
        })
    }

    /**
     * Get date string async and call next fun
     */
    private fun getDateFromItem(itemPos: Int) {
        val job = async {
            try {
                formatDate(viewModel.recentNotesList.value!![itemPos].dateToSearch)
            } catch (e: IndexOutOfBoundsException) {
                Timber.e(e)
                "14 avril 2000"
            }
        }
        launch(Dispatchers.Main) {
            updateTextAfterAsync(job.await().toString(), job.getCompletionExceptionOrNull())
        }
    }

    /**
     * Update toolbar date text after async task above
     */
    private fun updateTextAfterAsync(date: String?, e: Throwable?) {
        if (e != null) {
            Timber.e(e)
        } else {
            if (date.isNullOrEmpty()) {
                binding.dateselected.text = resources.getString(R.string.unknown)
            } else if (date != binding.dateselected.text) {
                val inAnim = AnimationUtils.loadAnimation(context, R.anim.slide_up)
                val outAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)

                binding.dateselected.startAnimation(outAnim)
                binding.dateselected.text = date
                binding.dateselected.startAnimation(inAnim)
            }
        }
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
            0 -> Snackbar.make(binding.root, "Already showing today's gratitude", Snackbar.LENGTH_SHORT).show()
            -1 -> Snackbar.make(binding.root, "Add a gratitude first", Snackbar.LENGTH_SHORT).show()
            else -> binding.recentNotesList.smoothScrollToPosition(0)
        }
    }

    /**
     * Open settings
     */

    private fun openSettings() {
        findNavController().navigate(HomeMainFragmentDirections.actionMainFragmentToSettingsFragment())
    }
}
