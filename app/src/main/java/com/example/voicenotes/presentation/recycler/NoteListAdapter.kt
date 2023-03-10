package com.example.voicenotes.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entity.NoteEntity
import com.example.voicenotes.databinding.ListitemBinding

class NoteListAdapter: ListAdapter<NoteEntity, NoteListAdapter.NoteItemViewHolder>(
    NoteItemDiffCallBack
) {

    var onNoteItemLongClickListener: ((NoteEntity) -> Unit) ? = null
    var onNoteItemClickListener: ((NoteEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        val binding = ListitemBinding.inflate(
            LayoutInflater.from(parent.context), parent,false
        )
        return NoteItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: NoteItemViewHolder, position: Int) {
        val noteItem = getItem(position)
        val binding = viewHolder.binding
        with(binding) {
            tvNoteTitle.text = noteItem.fileName
            tvNoteDate.text = noteItem.timesTamp
            tvFullTime.text = noteItem.duration
            root.setOnClickListener {
                onNoteItemClickListener?.invoke(noteItem)
            }
            root.setOnLongClickListener {
                onNoteItemLongClickListener?.invoke(noteItem)
                true
            }

        }
    }

    inner class NoteItemViewHolder(val binding: ListitemBinding):
        RecyclerView.ViewHolder(binding.root)
}