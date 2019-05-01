package me.martichou.be.grateful.ui.details

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionInflater
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.stfalcon.imageviewer.StfalconImageViewer
import me.martichou.be.grateful.R
import me.martichou.be.grateful.vo.Notes
import me.martichou.be.grateful.databinding.FragmentShowmainBinding
import me.martichou.be.grateful.util.GlideApp
import me.martichou.be.grateful.util.statusBarTrans
import me.martichou.be.grateful.util.statusBarWhite
import me.martichou.be.grateful.viewmodel.getNotesRepository
import me.martichou.be.grateful.viewmodel.getViewModel
import timber.log.Timber
import java.io.File

class ShowMainFragment : Fragment() {

    private val params by navArgs<ShowMainFragmentArgs>()
    private val viewModel by lazy {
        getViewModel { ShowViewModel(getNotesRepository(requireContext()), params.noteId) }
    }
    private lateinit var binding: FragmentShowmainBinding
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShowmainBinding.inflate(inflater, container, false)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move).apply {
            duration = 400
            interpolator = FastOutSlowInInterpolator()
        }

        binding.requestListener = imageListener

        // Set animation transition
        setupTransition()

        // Animation Watchdog - Make sure we don't wait longer than a second for the Glide image
        handler.postDelayed(1000) {
            startPostponedEnterTransition()
        }
        postponeEnterTransition()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.showModel = viewModel
        binding.args = params
        binding.hdl = this
    }

    /**
     * Set statusbar as translucent
     */
    override fun onResume() {
        Timber.d("onResume called")
        statusBarTrans(activity)
        super.onResume()
    }

    override fun onPause() {
        Timber.d("onPause called")
        statusBarWhite(activity)
        super.onPause()
    }

    /**
     * Setup fade out transition and sharedelementransition
     */
    private fun setupTransition() {
        enterTransition = Slide().apply { startDelay = 300 }
        returnTransition = Fade().apply {
            interpolator = FastOutLinearInInterpolator()
            duration = 75
        }
        exitTransition = Fade().apply { duration = 200 }
    }

    /**
     * Go to edit page button handler
     */
    fun editaction(view: View) {
        findNavController().navigate(ShowMainFragmentDirections.actionShowFragmentToEditFragment(params.noteId))
    }

    /**
     * Call animation once photo is loaded
     */
    private val imageListener = object : RequestListener<Drawable> {
        override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
        ): Boolean {
            startPostponedEnterTransition()
            return false
        }

        override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
        ): Boolean {
            startPostponedEnterTransition()
            return false
        }
    }

    fun openImageOfNote(view: View) {
        StfalconImageViewer.Builder<Notes>(context, mutableListOf(viewModel.note.value!!)) { vieww, image ->
            GlideApp.with(requireContext())
                .load(File(context?.getDir("imgForNotes", Context.MODE_PRIVATE), image.image))
                .into(vieww)
        }.withHiddenStatusBar(false)
        .show()
    }

}
