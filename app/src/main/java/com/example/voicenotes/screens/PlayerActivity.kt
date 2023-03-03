package com.example.voicenotes.screens

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.example.voicenotes.R
import com.example.voicenotes.app.NoteListApp
import com.example.voicenotes.databinding.ActivityPlayerBinding
import com.example.voicenotes.vm.ViewModelFactory
import javax.inject.Inject

class PlayerActivity : AppCompatActivity() {

    private val component by lazy {
        (application as NoteListApp).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val mediaPlayer by lazy {
        MediaPlayer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val filePath = intent.getStringExtra(EXTRA_FILE_PATH)
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME)

        mediaPlayer.apply {
            setDataSource(filePath)
            prepare()
        }
        playOrStopNote()

        binding.buttonPlayerPlayStop.setOnClickListener {
            playOrStopNote()
        }

    }

    private fun playOrStopNote() = with(binding) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            buttonPlayerPlayStop.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_round_pause_circle,
                theme
            )
        } else {
            mediaPlayer.pause()
            buttonPlayerPlayStop.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_round_play_circle,
                theme
            )
        }
    }



    companion object {

        private const val EXTRA_FILE_NAME = "extra_file_name"
        private const val EXTRA_FILE_PATH = "extra_file_path"

        fun newIntentStartPlayer(context: Context, filePath: String, fileName: String): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(EXTRA_FILE_PATH, filePath)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            return intent
        }

    }
}