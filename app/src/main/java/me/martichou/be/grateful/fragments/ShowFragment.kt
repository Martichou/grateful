package me.martichou.be.grateful.fragments

import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.ShowFragmentBinding
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.viewmodels.ShowViewModel

class ShowFragment : Fragment() {

    private val noteId: Long = ShowFragmentArgs.fromBundle(arguments).noteId

    private val viewModel by lazy {
        getViewModel { ShowViewModel(getNotesRepository(requireContext()), noteId) }
    }
    private lateinit var binding: ShowFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ShowFragmentBinding.inflate(inflater, container, false).apply {
            showModel = viewModel
            requestListener = imageListener
            setLifecycleOwner(this@ShowFragment)
        }

        postponeEnterTransition()

        roundShowImage()
        ViewCompat.setTransitionName(binding.shownoteImage, noteId.toString())

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.sharedimage_enter)

        return binding.root
    }

    private fun roundShowImage(){
        val image = binding.shownoteImage
        val curveRadius = 65F

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.outlineProvider = object : ViewOutlineProvider() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline?) {
                    outline?.setRoundRect(0, -curveRadius.toInt(), view.width,view.height, curveRadius)
                }
            }
            image.clipToOutline = true
        }
    }

    private val imageListener = object : RequestListener<Drawable> {
        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            startPostponedEnterTransition()
            return false
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
            startPostponedEnterTransition()
            return false
        }
    }

}
