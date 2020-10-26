package com.ulsee.ulti_a100.ui.streaming

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ulsee.ulti_a100.R
import java.util.*
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.IVLCVout

class VlcRtspActivity: AppCompatActivity() {

    val TAG = "VlcRtspActivity"

    companion object {
        val RTSP_URL = "rtspurl"
    }

    // display surface
    private var mSurface: SurfaceView? = null
    private var holder: SurfaceHolder? = null

    // media player
    private var libvlc: LibVLC? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mVideoWidth = 0
    private var mVideoHeight = 0
    private val VideoSizeChanged = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming)

        // Get URL
        val intent = intent
        val rtspUrl = intent.extras!!.getString(RTSP_URL)
        Log.d(TAG, "Playing back $rtspUrl")
        mSurface = findViewById<View>(R.id.surfaceView) as SurfaceView
        holder = mSurface!!.holder
        //holder.addCallback(this);
        val options = ArrayList<String>()
        options.add("--aout=opensles")
        options.add("--audio-time-stretch") // time stretching
        options.add("-vvv") // verbosity
        options.add("--aout=opensles")
        options.add("--avcodec-codec=h264")
        options.add("--file-logging")
        options.add("--logfile=vlc-log.txt")
        libvlc = LibVLC(applicationContext, options)
        holder?.setKeepScreenOn(true)

        // Create media player
        mMediaPlayer = MediaPlayer(libvlc)
        mMediaPlayer?.setEventListener(object : MediaPlayer.EventListener {
            override fun onEvent(event: MediaPlayer.Event) {
                when (event.type) {
                    MediaPlayer.Event.EndReached -> {
                        Log.i(TAG, "MediaPlayer.EventListener EndReached")
                        mMediaPlayer?.release()
                    }
                    MediaPlayer.Event.Playing, MediaPlayer.Event.Paused -> {
                    }
                    MediaPlayer.Event.Stopped -> Log.i(
                        TAG,
                        "MediaPlayer.EventListener Stopped"
                    )
                    MediaPlayer.Event.EncounteredError -> {
                        Log.i(TAG, "MediaPlayer.EventListener EncounteredError")
                        Toast.makeText(this@VlcRtspActivity, "RTSP ERROR", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    else -> Log.i(TAG, "MediaPlayer.EventListener default")
                }
            }
        })

        // Set up video output
        val vout: IVLCVout? = mMediaPlayer?.getVLCVout()
        vout?.setVideoView(mSurface)
        //vout.setSubtitlesView(mSurfaceSubtitles);
//        vout.addCallback(this);
        vout?.attachViews()
        val m = Media(libvlc, Uri.parse(rtspUrl))
        mMediaPlayer?.setMedia(m)
        mMediaPlayer?.play()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    fun releasePlayer() {
        if (libvlc == null) return
        mMediaPlayer?.stop()
        val vout: IVLCVout? = mMediaPlayer?.getVLCVout()
        //        vout.removeCallback(this);
        vout?.detachViews()
        holder = null
        libvlc?.release()
        libvlc = null
        mVideoWidth = 0
        mVideoHeight = 0
    }
}