package com.example.voicenotes.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.voicenotes.R
import com.example.voicenotes.app.NoteListApp
import com.example.voicenotes.databinding.ActivityMainBinding
import com.example.voicenotes.utils.*
import com.example.voicenotes.utils.Constants.FULL_DATE_PATTERN
import com.example.voicenotes.utils.Constants.REQUEST_CODE
import com.example.voicenotes.utils.Timer
import com.example.voicenotes.vm.NoteItemViewModel
import com.example.voicenotes.vm.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Timer.TimerTickListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val component by lazy {
        (application as NoteListApp).component
    }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[NoteItemViewModel::class.java]
    }
    private val timer by lazy {
        Timer(this)
    }
    private val vibrator by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private var durationInMillis = 0L
    private var startTime = 0L
    private var endTime = 0L
    private var permissionGranted = false
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private lateinit var date: String
    private var isRecording = false
    private var isPaused = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val activityContext = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestAudioPermission()
        init()
        buttonsActions()
    }

    private fun requestAudioPermission() {
        if (!isAudioPermissionGranted())
            showAudioPermissionDialog(REQUEST_CODE)
    }

    private fun init() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomMenuId.bottomMenu)
        bottomSheetBehavior.apply {
            peekHeight = 0
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun buttonsActions() = with(binding) {
        buttonRecord.setOnClickListener {

            when {
                isPaused -> resumeRecord()
                isRecording -> pauseRecord()
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        vibrator.vibrate(100);
                    startRecord()
                }
            }

        }

        buttonShowList.setOnClickListener {
            startActivity(NotesActivity.newIntentOpenNotesActivity(activityContext))
        }

        buttonDone.setOnClickListener {
            stopRecord()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomMenuBackground.visibility = View.VISIBLE
            binding.bottomMenuId.inputFileName.setText(filename)
        }

        buttonDelete.setOnClickListener {
            stopRecord()
            File(filePath(dirPath, filename)).delete()
            tvTimer.text = "00:00.0"
            showToast("Запись отменена")
        }

        bottomMenuId.buttonDeleteOnBottomMenu.setOnClickListener {
            File(filePath(dirPath, filename)).delete()
            tvTimer.text = "00:00.0"
            dismiss()
            showToast("Запись удалена")
        }
        bottomMenuId.buttonSaveOnBottomMenu.setOnClickListener {
            dismiss()
            saveNote()
        }

        bottomMenuBackground.setOnClickListener {
            File(filePath(dirPath, filename)).delete()
            dismiss()
        }
        buttonDelete.isClickable = false
    }

    private fun startRecord() = with(binding) {
        if (!isAudioPermissionGranted()) {
            showAudioPermissionDialog(REQUEST_CODE)
            return
        }
        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"

        val sdf = SimpleDateFormat(FULL_DATE_PATTERN, Locale.getDefault())
        date = sdf.format(Date())

        filename = returnDefaultFileName(date)
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(filePath(dirPath, filename))
            try {
                prepare()
            } catch (_: IOException) {}
            startTime = System.currentTimeMillis()
            start()
        }
        buttonRecord.setImageResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false
        timer.start()

        buttonDelete.isClickable = true
        buttonDelete.setImageResource(R.drawable.ic_delete)

        buttonShowList.visibility= View.GONE
        buttonDone.visibility= View.VISIBLE

    }
    private fun pauseRecord() {
        recorder.pause()
        isPaused = true
        binding.buttonRecord.setImageResource(R.drawable.ic_mic)
        timer.pause()
    }
    private fun resumeRecord() {
        recorder.resume()
        isPaused = false
        binding.buttonRecord.setImageResource(R.drawable.ic_pause)

        timer.start()
    }
    private fun stopRecord() = with(binding) {
        timer.stop()
        endTime = System.currentTimeMillis()
        durationInMillis = endTime - startTime
        recorder.apply {
            stop()
            release()
        }
        isPaused = false
        isRecording = false
        buttonShowList.visibility = View.VISIBLE
        buttonDone.visibility = View.GONE

        buttonDelete.isClickable = false
        buttonDelete.setImageResource(R.drawable.ic_delete_disabled)
        buttonRecord.setImageResource(R.drawable.ic_mic)
    }
    private fun dismiss() = with(binding) {
        bottomMenuBackground.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }, 100)
        hideKeyboard(bottomMenuId.inputFileName)
    }

    private fun saveNote() {
        val newFileName = binding.bottomMenuId.inputFileName.text.toString()
        if (newFileName != filename) {
            val newFile = File(filePath(dirPath, newFileName))
            File(filePath(dirPath, filename)).renameTo(newFile)
        }
        val filePath = filePath(dirPath, newFileName)
        viewModel.addNoteItem(
            fileName = newFileName,
            timesTamp = date,
            duration = durationInMillis,
            filePath = filePath


        )
        binding.tvTimer.text = "00:00.0"
    }
    override fun timerTick(duration: String) {
        binding.tvTimer.text = duration
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}