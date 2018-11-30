package me.martichou.be.grateful.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.data.NotesMinimal
import me.martichou.be.grateful.databinding.ListItemNotesBinding
import me.martichou.be.grateful.fragments.MainFragmentDirections

class NotesAdapter : ListAdapter<NotesMinimal, NotesAdapter.ViewHolder>(NotesDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.apply {
            bind(createOnClickListener(), note)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemNotesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun createOnClickListener(): OnNoteItemClickListener {
        return object : OnNoteItemClickListener {
            override fun onNoteItemClick(rootView: View, notes: NotesMinimal) {
                rootView.findNavController().navigate(
                        MainFragmentDirections.ActionNoteListFragmentToNoteDetailFragment(notes.id.toLong()),
                        FragmentNavigatorExtras(DataBindingUtil.getBinding<ListItemNotesBinding>(rootView)!!.showImageNote to notes.id))
            }
        }
    }

    class ViewHolder(private val binding: ListItemNotesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: OnNoteItemClickListener, item: NotesMinimal) {
            binding.apply {
                clickListener = listener
                note = item
                executePendingBindings()
            }
        }
    }

    interface OnNoteItemClickListener {
        fun onNoteItemClick(rootView: View, notes: NotesMinimal)
    }
}
