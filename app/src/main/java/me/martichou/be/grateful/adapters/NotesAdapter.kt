package me.martichou.be.grateful.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.ListItemNotesBinding
import me.martichou.be.grateful.fragments.MainFragmentDirections

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
                val direction = MainFragmentDirections
                        .ActionNoteListFragmentToNoteDetailFragment(notes.id)

                DataBindingUtil.getBinding<ListItemNotesBinding>(rootView)?.let {
                    val navigatorExtras = FragmentNavigatorExtras(it.showImageNote to notes.id.toString())
                    rootView.findNavController().navigate(direction, navigatorExtras)
                } ?: run {
                    // fail to getBinding for transition anim. we still proceed to navigate
                    rootView.findNavController().navigate(direction)
                }
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
        }
    }

    interface OnNoteItemClickListener {
        fun onNoteItemClick(rootView: View, notes: Notes)
    }
}
