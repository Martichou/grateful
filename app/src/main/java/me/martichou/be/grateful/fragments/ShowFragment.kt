package me.martichou.be.grateful.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.ShowFragmentBinding
import me.martichou.be.grateful.utilities.*
import me.martichou.be.grateful.viewmodels.ShowViewModel


class ShowFragment : Fragment() {

    private var noteId: Long = 0
    private val viewModel by lazy {
        getViewModel { ShowViewModel(getNotesRepository(requireContext()), noteId) }
    }
    private lateinit var binding: ShowFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noteId = ShowFragmentArgs.fromBundle(arguments!!).noteId
        binding = ShowFragmentBinding.inflate(inflater, container, false).apply {
            showModel = viewModel
            setLifecycleOwner(this@ShowFragment)
            ViewCompat.setTransitionName(shownoteImage, noteId.toString())
            requestListener = imageListener
        }

        postponeEnterTransition() // wait for glide callback
        setupTransition()

        return binding.root
    }

    private val imageListener = object : RequestListener<Drawable> {
        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            startPostponedEnterTransition()
            // Set translucent status bar
            statusBarTrans(requireActivity())
            return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
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
