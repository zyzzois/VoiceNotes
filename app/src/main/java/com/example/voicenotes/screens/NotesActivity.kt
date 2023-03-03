package com.example.voicenotes.screens

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.voicenotes.R
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

    private val mediaPlayer by lazy {
        MediaPlayer()
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
            showToast("long click")
        }
        onNoteItemPlayButtonCLickListener = {

            mediaPlayer.apply {
                setDataSource(it.filepath)
                prepare()
            }
        }
    }

//    private fun playOrStopNote() = with(binding) {
//
//        if (!mediaPlayer.isPlaying) {
//            mediaPlayer.start()
//
//            buttonPlayerPlayStop.background = ResourcesCompat.getDrawable(
//                resources,
//                R.drawable.ic_round_pause_circle,
//                theme
//            )
//            handler.postDelayed(runnable, delay)
//        } else {
//            mediaPlayer.pause()
//            buttonPlayerPlayStop.background = ResourcesCompat.getDrawable(
//                resources,
//                R.drawable.ic_round_play_circle,
//                theme
//            )
//            handler.removeCallbacks(runnable)
//        }
//    }


    companion object {
        fun newIntentOpenNotesActivity(context: Context) = Intent(context, NotesActivity::class.java)
    }
}