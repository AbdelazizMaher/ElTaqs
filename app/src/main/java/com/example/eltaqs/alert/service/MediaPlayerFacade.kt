package com.example.eltaqs.alert.service

import android.content.Context
import android.media.MediaPlayer
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
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
        }
    }
}