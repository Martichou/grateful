package me.martichou.be.grateful.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.RecyclerviewHomeitemBinding
import me.martichou.be.grateful.fragments.HomeMainFragmentDirections

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
