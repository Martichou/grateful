package me.martichou.be.grateful.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.RecyclerviewHomeitemBinding
import me.martichou.be.grateful.fragments.HomeMainFragmentDirections
import me.martichou.be.grateful.utilities.Size2
import timber.log.Timber
import java.io.File

class NotesAdapter : ListAdapter<Notes, NotesAdapter.ViewHolder>(NotesDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.apply {
            bind(createOnClickListener(), note)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerviewHomeitemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(): OnNoteItemClickListener {
        return object : OnNoteItemClickListener {
            override fun onNoteItemClick(rootView: View, notes: Notes) {
                val direction = HomeMainFragmentDirections.actionNoteListFragmentToNoteDetailFragment(notes.id)

                DataBindingUtil.getBinding<RecyclerviewHomeitemBinding>(rootView)?.let {
                    val navigatorExtras = FragmentNavigatorExtras(it.showImageNote to notes.id.toString())
                    rootView.findNavController().navigate(direction, navigatorExtras)
                } ?: run {
                    // fail to getBinding for transition anim. we still proceed to navigate
                    rootView.findNavController().navigate(direction)
                }
            }
        }
    }

    class ViewHolder(private val binding: RecyclerviewHomeitemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val set = ConstraintSet()

        fun bind(listener: OnNoteItemClickListener, item: Notes) {
            Glide.with(itemView.context)
                .`as`(Size2::class.java)
                .apply(sizeOptions)
                .load(File(itemView.context.getDir("imgForNotes", Context.MODE_PRIVATE), item.image))
                .into(object : SimpleTarget<Size2>() {
                    override fun onResourceReady(resource: Size2, transition: com.bumptech.glide.request.transition.Transition<in Size2>?) {
                        Timber.i("Size: $resource")
                        with(set) {
                            val posterRatio = String.format("%d:%d", resource.width, resource.height)
                            clone(binding.parentContsraint)
                            setDimensionRatio(binding.showImageNote.id, posterRatio)
                            applyTo(binding.parentContsraint)
                        }
                    }
                })

            binding.apply {
                clickListener = listener
                note = item
                executePendingBindings()
            }
        }

        private val sizeOptions by lazy {
            RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        }
    }

    interface OnNoteItemClickListener {
        fun onNoteItemClick(rootView: View, notes: Notes)
    }
}
