package me.martichou.be.grateful.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.ListItemNotesBinding
import me.martichou.be.grateful.fragments.MainFragmentDirections
import me.martichou.be.grateful.utilities.GlideApp
import java.io.File

class NotesAdapter : ListAdapter<Notes, NotesAdapter.ViewHolder>(NotesDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.apply {
            bind(createOnClickListener(), note)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(): OnNoteItemClickListener {
        return object : OnNoteItemClickListener {
            override fun onNoteItemClick(rootView: View, notes: Notes) {
                rootView.findNavController().navigate(
                        MainFragmentDirections.ActionNoteListFragmentToNoteDetailFragment(notes.id),
                        FragmentNavigatorExtras(DataBindingUtil.getBinding<ListItemNotesBinding>(rootView)!!.showImageNote to notes.id.toString()))
            }
        }
    }

    class ViewHolder(private val binding: ListItemNotesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: OnNoteItemClickListener, item: Notes) {
            binding.apply {
                clickListener = listener
                note = item
                executePendingBindings()
            }

            GlideApp.with(itemView.context)
                    .load(File(itemView.context.getDir("imgForNotes", Context.MODE_PRIVATE), item.image))
                    .override(binding.showImageNote.width, binding.showImageNote.height).fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.2f)
                    .into(binding.showImageNote)
        }
    }

    interface OnNoteItemClickListener {
        fun onNoteItemClick(rootView: View, notes: Notes)
    }
}
