package me.martichou.be.grateful.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.ListItemNotesBinding
import me.martichou.be.grateful.fragments.MainFragmentDirections

class NotesAdapter : ListAdapter<Notes, NotesAdapter.ViewHolder>(NotesDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = getItem(position)
        holder.apply {
            bind(createOnClickListener(note.id), note)
            itemView.tag = note
        }
    }

    /**
     * Inflate the layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemNotesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Event called on the click of one element of the recyclerview
     */
    private fun createOnClickListener(noteId: Long): View.OnClickListener {
        return View.OnClickListener {
            it.findNavController().navigate(MainFragmentDirections.ActionNoteListFragmentToNoteDetailFragment(noteId))
        }
    }

    class ViewHolder(
        private val binding: ListItemNotesBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, item: Notes) {
            binding.apply {
                /**
                 * Set the data binding variable from list_item_notes
                 */
                clickListener = listener
                n = item
                executePendingBindings()
            }
        }
    }
}