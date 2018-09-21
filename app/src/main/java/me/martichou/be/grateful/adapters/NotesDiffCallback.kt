package me.martichou.be.grateful.adapters

import androidx.recyclerview.widget.DiffUtil
import me.martichou.be.grateful.data.NotesMinimal

class NotesDiffCallback : DiffUtil.ItemCallback<NotesMinimal>() {

    override fun areItemsTheSame(oldItem: NotesMinimal, newItem: NotesMinimal): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NotesMinimal, newItem: NotesMinimal): Boolean {
        return oldItem == newItem
    }
}