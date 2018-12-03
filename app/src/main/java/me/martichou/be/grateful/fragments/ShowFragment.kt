package me.martichou.be.grateful.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import me.martichou.be.grateful.databinding.ShowFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.utilities.MoveViews
import me.martichou.be.grateful.viewmodels.ShowViewModel

class ShowFragment : Fragment() {

    private lateinit var viewModel: ShowViewModel
    private lateinit var binding: ShowFragmentBinding

    private var noteId: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noteId = ShowFragmentArgs.fromBundle(arguments).noteId
        viewModel = ViewModelProviders.of(this, InjectorUtils.provideShowViewModelFactory(requireContext().applicationContext, noteId)).get(ShowViewModel::class.java)
        binding = ShowFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@ShowFragment)
        }

        binding.showModel = viewModel
        binding.requestListener = imageListener

        ViewCompat.setTransitionName(binding.showImageNote, noteId.toString())

        postponeEnterTransition()
        sharedElementEnterTransition = MoveViews().apply {
            interpolator = FastOutSlowInInterpolator()
        }

        return binding.root
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
