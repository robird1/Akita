package com.ulsee.shiba.ui.streaming

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ulsee.shiba.R
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import java.io.IOException
import java.util.*

private const val USE_TEXTURE_VIEW = false
private const val ENABLE_SUBTITLES = true


class VlcRtspActivity: AppCompatActivity() {

    val TAG = "VlcRtspActivity"

    companion object {
        val RTSP_URL = "rtspurl"
    }

    // display surface
    private lateinit var mSurface: SurfaceView
    private var holder: SurfaceHolder? = null

    // media player
    private lateinit var libvlc: LibVLC
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var rtspUrl: String
    private lateinit var mVideoLayout: VLCVideoLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming2)

        // Get URL
        val intent = intent
        rtspUrl = intent.extras!!.getString(RTSP_URL) + ":554/h264/ch01/main/av_stream"
        Log.d(TAG, "Playing back $rtspUrl")
//        mSurface = findViewById<View>(R.id.surfaceView) as SurfaceView
//        holder = mSurface.holder
//        //holder.addCallback(this);
//        holder?.setKeepScreenOn(true)
        val options = ArrayList<String>()
        options.add("--aout=opensles")
        options.add("--audio-time-stretch") // time stretching
        options.add("-vvv") // verbosity
        options.add("--avcodec-codec=h264")
        options.add("--file-logging")
        options.add("--logfile=vlc-log.txt")
        libvlc = LibVLC(applicationContext, options)
        // Create media player
        mMediaPlayer = MediaPlayer(libvlc)
        setPlayerCallback()
        mVideoLayout = findViewById(R.id.video_layout)
    }

    override fun onStart() {
        Log.d(TAG, "[Enter] onStart")
        super.onStart()
        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW)
        try {
            val media = Media(libvlc, Uri.parse(rtspUrl))
            mMediaPlayer.media = media
            media.release()
        } catch (e: IOException) {
//            throw RuntimeException("Invalid asset folder")
            Log.d(TAG, "IOException e.message: ${e.message}")
        }
        mMediaPlayer.play()
    }

    override fun onStop() {
        Log.d(TAG, "[Enter] onStop")
        super.onStop()
        mMediaPlayer.stop()
        mMediaPlayer.detachViews()
    }

    override fun onDestroy() {
        Log.d(TAG, "[Enter] onDestroy")
        releasePlayer()
//        Log.d(TAG, "mMediaPlayer.isReleased: ${mMediaPlayer.isReleased}")
        super.onDestroy()
    }

    private fun releasePlayer() {
        if (!mMediaPlayer.isReleased) {
            mMediaPlayer.release()
        }
        if (!libvlc.isReleased) {
            libvlc.release()
        }
    }

    private fun setPlayerCallback() {
        mMediaPlayer.setEventListener { event ->
            when (event.type) {
                MediaPlayer.Event.EndReached -> {
                    Log.i(TAG, "MediaPlayer.EventListener EndReached")
//                    mMediaPlayer.release()
                }
                MediaPlayer.Event.Playing -> {
                    Log.i(TAG, "MediaPlayer.EventListener Playing")
                }
                MediaPlayer.Event.Paused -> {
                    Log.i(TAG, "MediaPlayer.EventListener Paused")
                }
                MediaPlayer.Event.Stopped -> Log.i(
                    TAG,
                    "MediaPlayer.EventListener Stopped"
                )
                MediaPlayer.Event.EncounteredError -> {
                    Log.i(TAG, "MediaPlayer.EventListener EncounteredError")
                    releasePlayer()
                    Toast.makeText(this@VlcRtspActivity, "RTSP ERROR", Toast.LENGTH_LONG).show()
                    finish()
                }
//                else -> Log.i(TAG, "MediaPlayer.EventListener default")
            }
        }
    }

}