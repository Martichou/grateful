package me.martichou.be.grateful.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.databinding.RecyclerviewHomeitemBinding
import me.martichou.be.grateful.util.Event
import me.martichou.be.grateful.vo.Notes
import timber.log.Timber

class NotesAdapter : ListAdapter<Notes, NotesAdapter.ViewHolder>(NotesDiffCallback()) {

    private val _openNote = MutableLiveData<Event<Pair<Notes, View>>>()
    internal val openNote: LiveData<Event<Pair<Notes, View>>> = _openNote

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
                _openNote.value = Event(Pair(notes, rootView))
            }
        }
    }

    class ViewHolder(private val binding: RecyclerviewHomeitemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: OnNoteItemClickListener, item: Notes) {
            binding.apply {
                clickListener = listener
                note = item
                executePendingBindings()
            }
            Timber.d("Notes ID: ${item.id}")
        }
    }

    interface OnNoteItemClickListener {
        fun onNoteItemClick(rootView: View, notes: Notes)
    }

    companion object {
        private const val TYPE_DATE = 1
        private const val TYPE_SCDDATE = 2
    }
}
