package com.example.voicenotes.utils

import android.os.Handler
import android.os.Looper

class Timer(listener: TimerTickListener) {

    interface TimerTickListener {
        fun timerTick(duration: String)
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var duration = 0L
    private var delay = 100L

    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable, delay)
            listener.timerTick(format())
        }
    }

    fun start() {
        handler.postDelayed(runnable, delay)
    }

    fun pause() {
        handler.removeCallbacks(runnable)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        duration = 0L
    }

    private fun format(): String {
        val milliseconds = duration % 1000
        val seconds = (duration / 1000) % 60
        val minutes = (duration / 60000) % 60
        val hours = (duration / 3600000)

        return if (hours > 0)
            "%02d:%02d:%02d.%02d".format(hours, minutes, seconds, milliseconds/10)
        else
            "%02d:%02d.%02d".format(minutes, seconds, milliseconds/100)
    }

}