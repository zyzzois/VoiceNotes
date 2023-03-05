package com.example.voicenotes.screens

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.voicenotes.R
import com.example.voicenotes.app.NoteListApp
import com.example.voicenotes.databinding.ActivityPlayerBinding
import com.example.voicenotes.stc.SpeechTextClass
import com.example.voicenotes.vm.ViewModelFactory
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Inject
import java.lang.Runnable

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

    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var delay = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupCustomToolbar()
        val filePath = intent.getStringExtra(EXTRA_FILE_PATH)
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME)
        val fileDate = intent.getStringExtra(EXTRA_FILE_DATE)
        binding.tvPlayerNoteTitle.text = fileName
        binding.tvPlayerNoteDate.text = fileDate

        mediaPlayer.apply {
            setDataSource(filePath)
            prepare()
        }

        binding.tvDuration.text = convertDate(mediaPlayer.duration)
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            binding.seekBar.progress = mediaPlayer.currentPosition
            binding.tvCurrentTime.text = convertDate(mediaPlayer.currentPosition)
            handler.postDelayed(runnable, delay)
        }

        binding.buttonPlayerPlayStop.setOnClickListener {
            playOrStopNote()
        }

        playOrStopNote()
        binding.seekBar.max = mediaPlayer.duration

        mediaPlayer.setOnCompletionListener {
            binding.buttonPlayerPlayStop.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_round_play_circle,
                theme
            )
            handler.removeCallbacks(runnable)
        }

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.buttonTextToSpeech.setOnClickListener {
            startSpeechTranscription(filePath)
        }
    }

    private fun startSpeechTranscription(filePath: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                SpeechTextClass(filePath).transcript
            }
            binding.tvNoteContent.visibility = View.VISIBLE
            binding.blast.visibility = View.GONE
            binding.tvNoteContent.text = result
        }
    }


    private fun playOrStopNote() = with(binding) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            if (mediaPlayer.audioSessionId != -1)
                blast.setAudioSessionId(mediaPlayer.audioSessionId);

            buttonPlayerPlayStop.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_round_pause_circle,
                theme
            )
            handler.postDelayed(runnable, delay)
        } else {
            mediaPlayer.pause()
            buttonPlayerPlayStop.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_round_play_circle,
                theme
            )
            handler.removeCallbacks(runnable)
        }
    }
    private fun convertDate(duration: Int): String {
        val tmp = duration/100
        val seconds = tmp%60
        val minutes = (tmp/60 % 60)
        val hours = ((tmp - minutes*60)/360)
        val formatted: NumberFormat = DecimalFormat("00")
        var str = "$minutes:${formatted.format(seconds)}"
        if (hours > 0)
            str = "$hours:$str"
        return str
    }
    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer.stop()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }
    private fun setupCustomToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    companion object {
        private const val EXTRA_FILE_NAME = "extra_file_name"
        private const val EXTRA_FILE_PATH = "extra_file_path"
        private const val EXTRA_FILE_DATE = "extra_file_date"

        fun newIntentStartPlayer(
            context: Context,
            filePath: String,
            fileName: String,
            date: String
        ): Intent {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(EXTRA_FILE_PATH, filePath)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            intent.putExtra(EXTRA_FILE_DATE, date)
            return intent
        }
    }
}