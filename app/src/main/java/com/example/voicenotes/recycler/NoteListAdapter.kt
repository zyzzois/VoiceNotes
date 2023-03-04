package com.example.voicenotes.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entity.NoteEntity
import com.example.voicenotes.R
import com.example.voicenotes.databinding.ListitemBinding
import java.text.SimpleDateFormat

class NoteListAdapter(private val context: Context): ListAdapter<NoteEntity, NoteListAdapter.NoteItemViewHolder>(NoteItemDiffCallBack) {

    private var editMode = false

    fun setEditMode(mode: Boolean) {
        if (editMode != mode) {
            editMode = mode
            notifyDataSetChanged()
        }
    }


    fun isEditMode() = editMode

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


            if (editMode) {
                checkBox.visibility = View.VISIBLE
                buttonPlay.visibility = View.GONE
            } else {
                checkBox.visibility = View.GONE
                buttonPlay.visibility = View.VISIBLE
            }
            checkBox.isChecked = noteItem.isChecked

        }
    }



    inner class NoteItemViewHolder(val binding: ListitemBinding):
        RecyclerView.ViewHolder(binding.root)
}