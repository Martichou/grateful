package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.recyclerView.DividerRV
import me.martichou.be.grateful.recyclerView.NotesAdapter
import me.martichou.be.grateful.utilities.formatDate
import me.martichou.be.grateful.utilities.makeToast
import me.martichou.be.grateful.utilities.statusBarWhite
import me.martichou.be.grateful.viewmodels.MainViewModel
import me.martichou.be.grateful.viewmodels.getNotesRepository
import me.martichou.be.grateful.viewmodels.getViewModel
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class HomeMainFragment : Fragment(), CoroutineScope {

    // TODO: IF NO IMAGE ADD A COLOR - SECOND TYPE OF RV ITEM

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
        binding.recentNotesList.addItemDecoration(DividerRV())

        // Set adapter to the recyclerview once other things are set
        subscribeUirecentNotesList(viewModel.adapter)
        binding.recentNotesList.adapter = viewModel.adapter

        // Wait RecyclerView layout for detail to list image return animation
        binding.recentNotesList.doOnLayout {
            startPostponedEnterTransition()
        }

        // Update subtitle while scrolling
        setupScrollRvListener()
    }

    /**
     * Set the status bar as white
     */
    override fun onResume() {
        super.onResume()
        statusBarWhite(activity)
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
     * Observe recentNotesList for and update adapter
     */
    private fun subscribeUirecentNotesList(adapter: NotesAdapter) {
        viewModel.recentNotesList.observe(viewLifecycleOwner, Observer { notes ->
            adapter.submitList(notes)
        })
    }

    /**
     * Setup fade out transition and sharedelementransition
     */
    private fun setupTransition() {
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        exitTransition = Fade().apply {
            excludeTarget(binding.appBar, true)
            duration = 150
        }
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

                val itemPosition = (binding.recentNotesList.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)

                getDateFromItem(itemPosition[0])
            }
        })
    }

    /**
     * Get date string async and call next fun
     */
    private fun getDateFromItem(itemPos: Int){
        val job = async {
            try {
                formatDate(viewModel.recentNotesList.value!![itemPos].dateToSearch)
            } catch (e: IndexOutOfBoundsException) {
                Timber.e(e)
                null
            }
        }
        launch(Dispatchers.Main) {
            updateTextAfterAsync(job.await().toString(), job.getCompletionExceptionOrNull())
        }
    }

    /**
     * Update toolbar date text after async task above
     */
    private fun updateTextAfterAsync(date: String?, e: Throwable?){
        if (e != null){
            makeToast(requireContext(), "We're sorry, something went wrong")
            Timber.e(e)
        } else {
            if (date.isNullOrEmpty()){
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
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
    }

    /**
     * Scroll to top of the list (today)
     */
    fun gototop(view: View) {
        val item = (binding.recentNotesList.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)[0]
        Timber.d("Item $item")
        when (item) {
            0 -> makeToast(requireContext(), "Already showing today's gratitude")
            -1 -> makeToast(requireContext(), "Add a gratitude first !")
            else -> binding.recentNotesList.smoothScrollToPosition(0)
        }
    }
}