package me.martichou.be.grateful.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentShowmainBinding
import me.martichou.be.grateful.utilities.AnimUtils
import me.martichou.be.grateful.utilities.MoveViews
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.utilities.statusBarTrans
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
            lifecycleOwner = this@ShowMainFragment
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
            statusBarTrans(activity)
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
            statusBarTrans(activity)
            return false
        }
    }

    private fun setupTransition() {
        // Animations when List entering Detail
        sharedElementEnterTransition = MoveViews().apply {
            interpolator = LinearOutSlowInInterpolator()
            duration = 300
        }
        enterTransition = Slide().apply {
            startDelay = 250
        }

        // Animations when Detail retuning to List
        sharedElementReturnTransition = MoveViews().apply {
            interpolator = FastOutLinearInInterpolator()
            duration = 250
        }
        returnTransition = Slide().apply {
            interpolator = AnimUtils.getFastOutLinearInInterpolator()
            duration = resources.getInteger(R.integer.config_duration_area_small).toLong()
        }
    }
}
