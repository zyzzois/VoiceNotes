package com.example.voicenotes

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.voicenotes.databinding.ActivityMainBinding
import com.example.voicenotes.databinding.CustomBottomMenuBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Timer.TimerTickListener {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var permissionGranted = false
    private lateinit var timer: Timer
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var isPaused = false
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val activityContext = this@MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestAudioPermission()
        init()
        buttonsActions()
    }

    private fun requestAudioPermission() {
        if (!isAudioPermissionGranted(activityContext))
            showAudioPermissionDialog(activityContext, REQUEST_CODE)
    }

    private fun init() {
        timer = Timer(activityContext)
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
                else -> startRecord()
            }
        }
        buttonShowList.setOnClickListener {
            showToast(activityContext, "List button")
        }
        buttonDone.setOnClickListener {
            stopRecord()
            showToast(activityContext, "Record saved")
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomMenuBackground.visibility = View.VISIBLE
            //
            binding.bottomMenuId.inputFileName.setText(filename)
        }
        buttonDelete.setOnClickListener {
            stopRecord()
            File("$dirPath$filename.mp3").delete()
            showToast(activityContext, "Record deleted")

        }

        bottomMenuId.buttonDeleteOnBottomMenu.setOnClickListener {
            File("$dirPath$filename.mp3").delete()
            dismiss()
        }
        bottomMenuId.buttonSaveOnBottomMenu.setOnClickListener {
            dismiss()
            saveNote()
        }

        bottomMenuBackground.setOnClickListener {
            File("$dirPath$filename.mp3").delete()
            dismiss()
        }
        buttonDelete.isClickable = false
    }

    private fun startRecord() = with(binding) {
        if (!isAudioPermissionGranted(activityContext)) {
            showAudioPermissionDialog(activityContext, REQUEST_CODE)
            return
        }
        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"
        val sdf = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        val date = sdf.format(Date())
        filename = "audio_record_$date"
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")
            try {
                prepare()
            } catch (_: IOException) {}

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

        tvTimer.text = "00:00.0"
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
            val newFile = File("$dirPath$newFileName.mp3")
            File("$dirPath$filename.mp3").renameTo(newFile)
        }
    }

    override fun timerTick(duration: String) {
        binding.tvTimer.text = duration
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val REQUEST_CODE = 200
    }
}