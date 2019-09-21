package me.martichou.be.grateful.ui.home

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
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
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.common.reflect.Reflection.getPackageName
import com.wooplr.spotlight.SpotlightView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.databinding.RecyclerviewHomeitemBinding
import me.martichou.be.grateful.databinding.RecyclerviewHomeitemSecondBinding
import me.martichou.be.grateful.di.Injectable
import me.martichou.be.grateful.ui.add.AddMainFragment
import me.martichou.be.grateful.util.DividerRV
import me.martichou.be.grateful.util.EventObserver
import me.martichou.be.grateful.util.autoCleared
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
class HomeMainFragment : Fragment(), androidx.appcompat.widget.Toolbar.OnMenuItemClickListener, Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mDelayHandler: Handler
    private var binding by autoCleared<FragmentHomemainBinding>()
    private var adapter by autoCleared<Any>()
    private var withdate: Boolean = true

    private var opening = false

    @SuppressLint("RestrictedApi")
    private val mRunnable = Runnable {
        if (mainViewModel.recentNotesList.value != null && mainViewModel.recentNotesList.value!!.size >= 3 && !sharedPreferences.getBoolean("alreadyasked", false)) {
            val shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
            // Inflate a banner in the view or in the list of notes
            binding.fab.visibility = View.INVISIBLE
            binding.askforreview.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f)
                        .setDuration(shortAnimationDuration.toLong())
                        .setListener(null)
            }
        }
    }

    fun btnPositive(view: View) {
        val textView = (view as MaterialButton)
        if (textView.text == resources.getString(R.string.firstaskpositive)) {
            binding.titleofreview.text = resources.getString(R.string.scdasktitle)
            binding.asknegative.text = resources.getString(R.string.scdasknegative)
            textView.text = resources.getString(R.string.scdaskpositive)
        } else {
            // Go to play store
            val appPackageName = context?.packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")));
            } catch (anfe: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")));
            }
            sharedPreferences.edit().putBoolean("alreadyasked", true).apply()
            btnNegative(binding.asknegative)
        }
    }

    fun btnNegative(view: View) {
        val shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        binding.askforreview.apply {
            alpha = 1f

            animate().alpha(0f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(object: Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        @SuppressLint("RestrictedApi")
                        override fun onAnimationEnd(animation: Animator?) {
                            visibility = View.GONE
                            binding.fab.visibility = View.VISIBLE
                            // Set as never ask again
                            sharedPreferences.edit().putBoolean("alreadyasked", true).apply()
                        }
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                    })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentHomemainBinding.inflate(inflater, container, false).apply {
            // Setup toolbar menu item click listener
            // Don't know why setHasOptionMenu don't work
            toolbar.setOnMenuItemClickListener(this@HomeMainFragment)

            recentNotesList.setHasFixedSize(true)
            recentNotesList.addItemDecoration(DividerRV(requireContext()))
            recentNotesList.setItemViewCacheSize(20)
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        withdate = !sharedPreferences.getBoolean("fullwidth", false)

        mDelayHandler = Handler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        // Set adapter to the recyclerview once other things are set
        adapter = if (withdate)
            NotesAdapter()
        else
            NotesAdapterSecond()

        binding.apply {
            thisVm = mainViewModel
            recentNotesList.adapter = adapter as RecyclerView.Adapter<*>
            lifecycleOwner = viewLifecycleOwner
            hdl = this@HomeMainFragment
        }

        // Subscribe adapter
        subscribeUirecentNotesList(adapter)
    }

    override fun onPause() {
        super.onPause()
        mDelayHandler.removeCallbacks(mRunnable)
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
    private fun subscribeUirecentNotesList(a: Any) {
        // Fill adapter item list
        val adapter = if (withdate)
            (a as NotesAdapter)
        else
            (a as NotesAdapterSecond)

        mainViewModel.recentNotesList.observe(viewLifecycleOwner, Observer { notes ->
            if (notes.isNullOrEmpty()) {
                adapter.submitList(null)
                binding.apply {
                    loadingUi.visibility = View.GONE
                    nonethinking.visibility = View.VISIBLE
                }

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
                Timber.d("DBE: Asking...")
                mDelayHandler.postDelayed(mRunnable, 1000)
            }
        })

        // Handle click on item list
        if (withdate)
            (a as NotesAdapter).openNote.observe(viewLifecycleOwner, EventObserver { pair ->
                if (!opening) {
                    opening = true
                    val direction = HomeMainFragmentDirections.actionNoteListFragmentToNoteDetailFragment(pair.first.id)
                    DataBindingUtil.getBinding<RecyclerviewHomeitemBinding>(pair.second).let {
                        val navigatorExtras = FragmentNavigatorExtras(it!!.showImageNote to pair.first.id.toString())
                        findNavController().navigate(direction, navigatorExtras)
                    }
                }
            })
        else
            (a as NotesAdapterSecond).openNote.observe(viewLifecycleOwner, EventObserver { pair ->
                if (!opening) {
                    opening = true
                    val direction = HomeMainFragmentDirections.actionNoteListFragmentToNoteDetailFragment(pair.first.id)
                    DataBindingUtil.getBinding<RecyclerviewHomeitemSecondBinding>(pair.second).let {
                        val navigatorExtras = FragmentNavigatorExtras(it!!.showImageNote to pair.first.id.toString())
                        findNavController().navigate(direction, navigatorExtras)
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
        when ((binding.recentNotesList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()) {
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
