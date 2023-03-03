package com.example.voicenotes.screens

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
        buttonsActions()
    }

    private fun buttonsActions() {
        binding.buttonAddNote.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        with(binding.rcView) {
            noteListAdapter = NoteListAdapter()
            adapter = noteListAdapter
        }
        viewModel.noteList.observe(this) {
            noteListAdapter.submitList(it)
        }
        setupClickListener()
    }

    private fun setupClickListener() {
        noteListAdapter.onNoteItemClickListener = {
            val intent = PlayerActivity.newIntentStartPlayer(this, it.filepath, it.fileName)
            startActivity(intent)
        }
        noteListAdapter.onNoteItemLongClickListener = {
            showToast(this, "long click")
        }
    }

    companion object {
        fun newIntentOpenNotesActivity(context: Context) = Intent(context, NotesActivity::class.java)
    }
}