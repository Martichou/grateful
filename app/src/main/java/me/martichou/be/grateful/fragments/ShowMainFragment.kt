package me.martichou.be.grateful.fragments

import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.Fade
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

class ShowMainFragment : Fragment() {

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
            ViewCompat.setTransitionName(shownoteImage, noteId.toString())
            requestListener = imageListener
        }

        postponeEnterTransition() // wait for glide callback
        setupTransition()
        roundShowImage()

        return binding.root
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

    private fun roundShowImage() {
        binding.bgView.outlineProvider = object : ViewOutlineProvider() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun getOutline(view: View, outline: Outline?) {
                outline?.setRoundRect(0, 0, view.width, view.height, 65F)
            }
        }
        binding.bgView.clipToOutline = true
    }

}
