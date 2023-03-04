package com.example.voicenotes.screens

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entity.NoteEntity
import com.example.voicenotes.app.NoteListApp
import com.example.voicenotes.databinding.ActivityNotesBinding
import com.example.voicenotes.recycler.NoteListAdapter
import com.example.voicenotes.utils.showToast
import com.example.voicenotes.vm.MainViewModel
import com.example.voicenotes.vm.ViewModelFactory
import javax.inject.Inject

class NotesActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val binding by lazy {
        ActivityNotesBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as NoteListApp).component
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private lateinit var noteListAdapter: NoteListAdapter
    private lateinit var deleteList: MutableList<NoteEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
        searchNoteAction()
    }

    private fun searchNoteAction() {
        binding.editTextSearchNote.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                searchInDb(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchInDb(query: String) {
        viewModel.searchInDataBase("%$query%")
        viewModel.searchedQueryList.observe(this) {
            noteListAdapter.submitList(it)
        }
    }

    private fun setupRecyclerView() {
        with(binding.rcView) {
            noteListAdapter = NoteListAdapter(this@NotesActivity)
            adapter = noteListAdapter
        }
        viewModel.noteList.observe(this) {
            noteListAdapter.submitList(it)
        }
        setupClickListener()
    }

    private fun setupClickListener() = with(noteListAdapter) {
        onNoteItemClickListener = {
            val intent = PlayerActivity.newIntentStartPlayer(
                this@NotesActivity,
                it.filepath,
                it.fileName
            )
            startActivity(intent)
        }

        onNoteItemLongClickListener = {
            //viewModel.setEditMode(true)
            val pos = currentList.indexOf(it)
            noteListAdapter.setEditMode(true)
            currentList[pos].isChecked = !currentList[pos].isChecked
        }


    }


    companion object {
        fun newIntentOpenNotesActivity(context: Context) = Intent(context, NotesActivity::class.java)
    }
}