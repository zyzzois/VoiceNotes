package com.example.voicenotes.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.text.DecimalFormat
import java.text.NumberFormat

private var permission = arrayOf(Manifest.permission.RECORD_AUDIO)

fun AppCompatActivity.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun AppCompatActivity.hideKeyboard(view: View) {
    val km = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    km.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.showAudioPermissionDialog(requestCode: Int) {
    ActivityCompat.requestPermissions(this, permission, requestCode)
}

fun AppCompatActivity.isAudioPermissionGranted(): Boolean {
    return ActivityCompat.checkSelfPermission(
        this, permission[0]
    ) == PackageManager.PERMISSION_GRANTED
}

fun filePath(dirPath: String, fileName: String): String {
    return dirPath+fileName+M4A
}

fun returnDefaultFileName(date: String) = AUDIO_RECORD_ + date

private const val M4A = ".m4a"
private const val AUDIO_RECORD_ = "note_"