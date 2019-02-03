package me.martichou.be.grateful.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import com.google.android.material.appbar.AppBarLayout
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.recyclerView.NotesAdapter
import me.martichou.be.grateful.utilities.*
import me.martichou.be.grateful.viewmodels.MainViewModel
import timber.log.Timber

class HomeMainFragment : Fragment() {

    private val viewModel by lazy {
        getViewModel { MainViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: FragmentHomemainBinding
    private var isExpanded = false
    private var liststate: Parcelable? = null
    private val adapter = NotesAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomemainBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@HomeMainFragment)
            thisVm = viewModel
            hdl = this@HomeMainFragment
        }

        // Prepare recyclerview and bind
        binding.recentNotesList.setHasFixedSize(true)
        binding.recentNotesList.addItemDecoration(DividerRV())
        binding.recentNotesList.adapter = adapter
        subscribeUirecentNotesList(adapter)

        // Wait RecyclerView layout for detail to list image return animation
        postponeEnterTransition()
        binding.recentNotesList.doOnLayout {
            startPostponedEnterTransition()
        }

        // Set back statusbar to white and dark icon
        statusBarWhite(requireActivity())

        // Setup exit animation with a fadeout
        setupTransition()

        // Hide fab on scroll and bottomappbar too
        setupScrollListener()

        // Listen for bottomappbar's menu click
        setupBottomAppBarMenuListener()

        // Arrow rotation on offset change
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val progress = (-i).toFloat() / totalScrollRange
            binding.datePickerArrow.rotation = 180 + progress * 180
            isExpanded = i == 0
        })

        // Update subtitle while scrolling
        binding.recentNotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemPosition = (binding.recentNotesList.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)

                try {
                    if (binding.dateselected.text != formatDate(viewModel.getNotes().value!![itemPosition[0]].dateToSearch)) {

                        val inAnim = AnimationUtils.loadAnimation(context, R.anim.slide_up)
                        val outAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)

                        binding.dateselected.startAnimation(outAnim)

                        binding.dateselected.text = formatDate(viewModel.getNotes().value!![itemPosition[0]].dateToSearch)

                        binding.dateselected.startAnimation(inAnim)

                        binding.compactcalendarView.date = stringToDate(viewModel.getNotes().value!![itemPosition[0]].dateToSearch)!!.time
                    }
                }catch (e: IndexOutOfBoundsException){
                    Timber.d("FATAL ERROR, INDEX OUT OF BOUND $e")
                }
            }
        })

        return binding.root
    }

    // Temp workaround
    override fun onResume() {
        super.onResume()
        if (liststate != null) {
            Timber.d("Called onResume")
            // TODO - Fix sharedelement transition due to something idk
            // TODO - If the note wasn't showed in the first screen (when user hasn't scrolled yet)
            // TODO - then the sharedelementanimation is glitched.
            binding.recentNotesList.layoutManager?.onRestoreInstanceState(liststate)
        }
    }

    // Temp workaround
    override fun onPause() {
        super.onPause()
        liststate = binding.recentNotesList.layoutManager?.onSaveInstanceState()
    }

    /**
     * Observe recentNotesList for and update adapter
     */
    private fun subscribeUirecentNotesList(adapter: NotesAdapter) {
        viewModel.getNotes().observe(viewLifecycleOwner, Observer { notes ->
            if (notes.isNullOrEmpty()) {
                binding.recentNotesList.visibility = View.GONE
                binding.nonethinking.visibility = View.VISIBLE
            } else {
                adapter.submitList(notes)
                binding.recentNotesList.visibility = View.VISIBLE
                binding.nonethinking.visibility = View.GONE
            }
        })
    }

    /**
     * New note action
     */
    fun btnNewAction(view: View) {
        val bottomsheetFragment = AddMainFragment()
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
    }

    fun ooccalendar(view: View) {
        isExpanded = !isExpanded
        binding.appBar.setExpanded(isExpanded, true)
    }

    fun gototop(view: View) {
        if(0 == (binding.recentNotesList.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)[0]){
            makeToast(requireContext(), "Already showing today's gratitude")
        }else {
            binding.recentNotesList.smoothScrollToPosition(0)
        }
    }

    private fun setupBottomAppBarMenuListener() {
        binding.bottomAppBar.setNavigationOnClickListener {
            makeToast(requireContext(), "Not yet implemented")
        }
    }

    private fun setupTransition() {
        exitTransition = Fade().apply {
            interpolator = AnimUtils.getFastOutSlowInInterpolator()
            duration = resources.getInteger(R.integer.config_duration_area_small).toLong()
        }
    }

    private fun setupScrollListener() {
        binding.recentNotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fab.visibility == View.VISIBLE) {
                    binding.fab.hide()
                    binding.bottomAppBar.animate().alpha(0.0f).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            binding.bottomAppBar.visibility = View.GONE
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            binding.shadow.visibility = View.GONE
                        }
                    }).duration = 200
                } else if (dy < 0 && binding.fab.visibility != View.VISIBLE) {
                    binding.bottomAppBar.animate().alpha(1.0f).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            binding.bottomAppBar.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            binding.shadow.visibility = View.VISIBLE
                        }
                    }).duration = 200
                    binding.fab.show()
                }
            }
        })
    }
}