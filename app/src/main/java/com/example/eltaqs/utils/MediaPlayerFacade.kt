package com.example.eltaqs.utils

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.eltaqs.R

object MediaPlayerFacade {
    private var mediaPlayer : MediaPlayer? = null

    fun playAudio(context : Context){
        mediaPlayer = MediaPlayer.create(
            context,
            R.raw.alert)
            .apply {
                isLooping = true
                start()
            }
    }

    fun stopAudio(){
        Log.d("MediaPlayerFacade", "stopAudio: ")
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
        }
    }
}