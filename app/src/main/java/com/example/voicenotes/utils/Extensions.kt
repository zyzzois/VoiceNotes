package com.example.voicenotes.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

private var permission = arrayOf(Manifest.permission.RECORD_AUDIO)

fun AppCompatActivity.showToast(context: Context, text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun AppCompatActivity.hideKeyboard(view: View) {
    val km = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    km.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.showAudioPermissionDialog(activity: Activity, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permission, requestCode)
}

fun AppCompatActivity.isAudioPermissionGranted(activity: Activity): Boolean {
    return ActivityCompat.checkSelfPermission(
        this, permission[0]
    ) == PackageManager.PERMISSION_GRANTED
}