package com.example.voicenotes.utils

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class Timer(
    private val listener: TimerTickListener,
    private val coroutineContext: CoroutineContext = Dispatchers.Main
) {

    interface TimerTickListener {
        fun timerTick(duration: String)
    }

    private var job: Job? = null
    private var duration = 0L
    private val delay = 100L

    fun start() {
        job = CoroutineScope(coroutineContext).launch {
            while (true) {
                delay(delay)
                duration += delay
                listener.timerTick(format())
            }
        }
    }

    fun pause() {
        job?.cancel()
    }

    fun stop() {
        job?.cancel()
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