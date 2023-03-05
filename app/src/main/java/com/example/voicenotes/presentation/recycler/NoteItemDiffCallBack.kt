package com.example.voicenotes.presentation.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.domain.entity.NoteEntity

object NoteItemDiffCallBack : DiffUtil.ItemCallback<NoteEntity>() {
    override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
        return oldItem == newItem
    }
}