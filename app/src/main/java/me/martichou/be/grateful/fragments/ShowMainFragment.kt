package me.martichou.be.grateful.fragments

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
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
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.FragmentShowmainBinding
import me.martichou.be.grateful.utilities.AnimUtils
import me.martichou.be.grateful.utilities.GlideApp
import me.martichou.be.grateful.viewmodels.ShowViewModel
import me.martichou.be.grateful.viewmodels.getNotesRepository
import me.martichou.be.grateful.viewmodels.getViewModel
import java.io.File

class ShowMainFragment : Fragment() {

    private val params by navArgs<ShowMainFragmentArgs>()
    private val viewModel by lazy {
        getViewModel { ShowViewModel(getNotesRepository(requireContext()), params.noteId) }
    }
    private lateinit var binding: FragmentShowmainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShowmainBinding.inflate(inflater, container, false)

        // Set animation transition w/ sharedelement
        setupTransition()

        // Wait for glide callback
        postponeEnterTransition()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.showModel = viewModel
        binding.args = params
        binding.hdl = this
        binding.requestListener = imageListener
    }

    /**
     * Setup fade out transition and sharedelementransition
     */
    private fun setupTransition() {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move).apply { duration = 250 }
        enterTransition = Slide().apply { startDelay = 250 }
        returnTransition = Fade().apply {
            interpolator = AnimUtils.getFastOutLinearInInterpolator()
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
                target: com.bumptech.glide.request.target.Target<Drawable>?,
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
