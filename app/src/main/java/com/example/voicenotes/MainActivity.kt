package com.example.voicenotes

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.example.voicenotes.databinding.ActivityMainBinding
import kotlinx.coroutines.NonCancellable
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var permissionGranted = false
    private var dirPath = ""
    private var fileName = ""
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private lateinit var recorder: MediaRecorder
    private var isRecording = false
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_AUDIO_PERMISSION_CODE)
        }

//        binding.b.setOnClickListener {
//            when {
//                isPaused -> resumeRecording()
//                isRecording -> pauseRecording()
//                else -> startRecording()
//            }
//        }

    }

    private fun pauseRecording() {
        recorder.pause()
        isPaused = true
    }

    private fun resumeRecording() {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startRecording() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_AUDIO_PERMISSION_CODE)
            return
        }

        recorder = MediaRecorder()
        dirPath = "${externalCacheDir?.absolutePath}/"

        val sdf = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        val date = sdf.format(Date())
        fileName = "audio_record_$date"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$fileName.mp3")
        }

        //чето странное тут творится
        try {
            Looper.prepare()

        } catch (e: IOException) {

        }
        NonCancellable.start()
        // start recording
    }





    companion object {
        const val REQUEST_AUDIO_PERMISSION_CODE = 200
    }
}