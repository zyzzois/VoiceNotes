package com.example.voicenotes.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entity.NoteEntity
import com.example.voicenotes.R
import com.example.voicenotes.databinding.ListitemBinding

class NoteListAdapter(private val context: Context): ListAdapter<NoteEntity, NoteListAdapter.NoteItemViewHolder>(NoteItemDiffCallBack) {

    private var editMode = false

    fun isEditMode() = editMode

    fun setEditMode(mode: Boolean) {
        if (mode != editMode)
            editMode = mode
    }

    var onNoteItemLongClickListener: ((NoteEntity) -> Unit)? = null
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
        val playIcon = AppCompatResources.getDrawable(context, R.drawable.ic_play)
        val stopIcon = AppCompatResources.getDrawable(context, R.drawable.ic_pause)

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
//            viewHolder.playButton.setOnClickListener {
//                currentPlayingNote?.apply {
//                    isPlaying = false
//                    notifyItemChanged(currentList.indexOf(this))
//                }
//                currentPlayingNote = noteItem
//                noteItem.isPlaying = true
//                notifyItemChanged(position)
//                onNoteItemPlayButtonCLickListener?.invoke(noteItem)
//            }
//
//            if (noteItem.isPlaying)
//                viewHolder.playButton.setImageDrawable(stopIcon)
//            else
//                viewHolder.playButton.setImageDrawable(playIcon)

            if (editMode) {
                binding.buttonPlay.isClickable = false
                binding.buttonPlay.visibility = View.GONE
                checkBox.visibility = View.VISIBLE
                checkBox.isChecked = noteItem.isChecked
            } else {
                binding.buttonPlay.isClickable = true
                binding.buttonPlay.visibility = View.VISIBLE
                checkBox.visibility = View.GONE
                checkBox.isChecked = false
            }
        }
    }

    inner class NoteItemViewHolder(val binding: ListitemBinding):
        RecyclerView.ViewHolder(binding.root) {
        val playButton = binding.buttonPlay
    }
}