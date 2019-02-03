package me.martichou.be.grateful.fragments

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_showmain.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentShowmainBinding
import me.martichou.be.grateful.utilities.*
import me.martichou.be.grateful.viewmodels.ShowViewModel

class ShowMainFragment : Fragment(), MotionLayout.TransitionListener {

    // TODO - only show thinking when no content
    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, progress: Float) {}

    private var noteId: Long = 0
    private val viewModel by lazy {
        getViewModel { ShowViewModel(getNotesRepository(requireContext()), noteId) }
    }
    private lateinit var binding: FragmentShowmainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noteId = ShowMainFragmentArgs.fromBundle(arguments!!).noteId
        binding = FragmentShowmainBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@ShowMainFragment)
            showModel = viewModel
            requestListener = imageListener
            hdl = this@ShowMainFragment
            motionLayout.setTransitionListener(this@ShowMainFragment)
            ViewCompat.setTransitionName(coverImage, noteId.toString())
        }

        postponeEnterTransition() // wait for glide callback
        setupTransition()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (binding.coverImage.transitionName.isNullOrEmpty())
            ViewCompat.setTransitionName(binding.coverImage, noteId.toString())
    }

    fun editaction(view: View) {
        findNavController().navigate(ShowMainFragmentDirections.actionShowFragmentToEditFragment(noteId))
    }

    private val imageListener = object : RequestListener<Drawable> {
        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            startPostponedEnterTransition()
            // Set translucent status bar
            statusBarTrans(requireActivity())
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: com.bumptech.glide.request.target.Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            startPostponedEnterTransition()
            // Set translucent status bar
            statusBarTrans(requireActivity())
            return false
        }
    }

    private fun setupTransition() {
        // Animations when List entering Detail
        sharedElementEnterTransition = MoveViews().apply {
            interpolator = AnimUtils.getFastOutSlowInInterpolator()
            duration = resources.getInteger(R.integer.config_duration_area_large_expand).toLong()
        }
        enterTransition = Fade().apply {
            interpolator = AnimUtils.getLinearOutSlowInInterpolator()
            startDelay = resources.getInteger(R.integer.config_duration_area_large_expand).toLong()
        }

        // Animations when Detail retuning to List
        sharedElementReturnTransition = MoveViews().apply {
            interpolator = AnimUtils.getFastOutSlowInInterpolator()
            duration = resources.getInteger(R.integer.config_duration_area_large_collapse).toLong()
        }
        returnTransition = Fade().apply {
            interpolator = AnimUtils.getFastOutLinearInInterpolator()
            duration = resources.getInteger(R.integer.config_duration_area_small).toLong()
        }
    }
}
