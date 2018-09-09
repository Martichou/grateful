package me.martichou.be.grateful.adapters

import android.support.v7.util.DiffUtil
import me.martichou.be.grateful.data.Notes

class NotesDiffCallback : DiffUtil.ItemCallback<Notes>() {

    override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
        return oldItem == newItem
    }
}