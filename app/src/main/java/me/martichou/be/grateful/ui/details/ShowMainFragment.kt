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
import androidx.lifecycle.ViewModelProvider
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
import me.martichou.be.grateful.databinding.FragmentShowmainBinding
import me.martichou.be.grateful.di.Injectable
import me.martichou.be.grateful.util.GlideApp
import me.martichou.be.grateful.util.statusBarTrans
import me.martichou.be.grateful.util.statusBarWhite
import java.io.File
import javax.inject.Inject

class ShowMainFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var showViewModel: ShowViewModel
    private val params by navArgs<ShowMainFragmentArgs>()
    private lateinit var binding: FragmentShowmainBinding
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentShowmainBinding.inflate(inflater, container, false).apply {
            requestListener = imageListener
        }

        setupTransition()

        // Animation Watchdog - Make sure we don't wait longer than a second for the Glide image
        handler.postDelayed(1000) {
            startPostponedEnterTransition()
        }
        postponeEnterTransition()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showViewModel = ViewModelProvider(this, viewModelFactory).get(ShowViewModel::class.java).also {
            it.setNote(params.noteId)
        }

        binding.apply {
            showModel = showViewModel
            lifecycleOwner = viewLifecycleOwner
            args = params
            hdl = this@ShowMainFragment
        }
    }

    /**
     * Set statusbar as translucent
     */
    override fun onResume() {
        super.onResume()
        statusBarTrans(activity)
    }

    override fun onPause() {
        super.onPause()
        statusBarWhite(activity)
    }

    /**
     * Setup fade out transition and sharedelementransition
     */
    private fun setupTransition() {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move).apply {
            duration = 300
            interpolator = FastOutSlowInInterpolator()
        }
        enterTransition = Slide().apply { startDelay = 250 }
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
        StfalconImageViewer.Builder(context, mutableListOf(showViewModel.note.value!!)) { vieww, image ->
            GlideApp.with(this)
                    .load(File(context?.getDir("imgForNotes", Context.MODE_PRIVATE), image.image))
                    .into(vieww)
        }.withHiddenStatusBar(false).show()
    }

}
