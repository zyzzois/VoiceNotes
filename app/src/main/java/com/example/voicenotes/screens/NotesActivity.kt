package com.example.voicenotes.screens

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entity.NoteEntity
import com.example.voicenotes.app.NoteListApp
import com.example.voicenotes.databinding.ActivityNotesBinding
import com.example.voicenotes.recycler.NoteListAdapter
import com.example.voicenotes.utils.showToast
import com.example.voicenotes.vm.MainViewModel
import com.example.voicenotes.vm.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private var allChecked = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecyclerView()
        searchNoteAction()
        setupActionBar()
        setupButtonsAction()
        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupButtonsAction() = with(binding) {
        buttonClose.setOnClickListener {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

            buttonsBar.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            // Сделать чекбоксы
            // noteListAdapter.setEditMode(false)
        }
        buttonSelectAllItems.setOnClickListener {
            allChecked = !allChecked
            // Сделать чекбоксы
            // обновить список
        }
        buttonAd.setOnClickListener {
            startActivity(Intent(this@NotesActivity, MainActivity::class.java))
        }
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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
            if (noteListAdapter.isEditMode()) {
                val pos = currentList.indexOf(it)
                currentList[pos].isChecked = !currentList[pos].isChecked
                notifyItemChanged(pos)
            } else {
                val intent = PlayerActivity.newIntentStartPlayer(
                    this@NotesActivity,
                    it.filepath,
                    it.fileName,
                    it.timesTamp
                )
                startActivity(intent)
            }

        }

        onNoteItemLongClickListener = {
            val pos = currentList.indexOf(it)
            noteListAdapter.setEditMode(true)
            currentList[pos].isChecked = !currentList[pos].isChecked
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            if (noteListAdapter.isEditMode() && binding.buttonsBar.visibility == View.GONE) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setDisplayShowHomeEnabled(false)

                binding.buttonsBar.visibility = View.VISIBLE
            }
        }


    }


    companion object {
        fun newIntentOpenNotesActivity(context: Context) = Intent(context, NotesActivity::class.java)
    }
}