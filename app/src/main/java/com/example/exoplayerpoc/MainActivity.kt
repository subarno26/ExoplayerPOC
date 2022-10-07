package com.example.exoplayerpoc

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var playbackPosition = 0L
    private var currentItem = 0
    private var playerReady = true
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeVariables()
    }

    //handling lifecycle callbacks by releasing memory when not in use.
    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initPlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initPlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializeVariables() {
        playerView = findViewById(R.id.player_view)
        progressBar = findViewById(R.id.buffering_progress)
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(this)
            .setSeekForwardIncrementMs(5000)
            .build()
            .also { exoplayer ->
                playerView.player = exoplayer
                val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
                val secondMediaItem = MediaItem.fromUri(getString(R.string.myTestMp4))
                //setting media item to exoplayer, resuming playback
                exoplayer.setMediaItem(mediaItem)
                exoplayer.addMediaItem(secondMediaItem)
                exoplayer.seekTo(currentItem, playbackPosition)
                exoplayer.addListener(playbackStateListener())
                exoplayer.playWhenReady = playerReady
                exoplayer.prepare()
            }
    }

    private fun fetchMediaHLSFormat(): MediaItem {
        return MediaItem.Builder()
            .setUri(getString(R.string.hls_media))
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
    }

    private fun fetchMediaDashFormat(): MediaItem {
        return MediaItem.Builder()
            .setUri(getString(R.string.media_url_dash))
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()
    }


    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playerReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener())
            exoPlayer.release()
        }
        player = null
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    //Checking playback states
    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            var playerState: String
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    playerState = "STATE_IDLE"
                }
                ExoPlayer.STATE_BUFFERING -> {
                    playerState = "STATE_BUFFERING"
                    progressBar.visibility = View.VISIBLE
                }
                ExoPlayer.STATE_READY -> {
                    playerState = "STATE_READY"
                    progressBar.visibility = View.GONE
                }
                ExoPlayer.STATE_ENDED -> {
                    playerState = "STATE_ENDED"
                }
                else -> {
                    playerState = "UNKNOWN_STATE"
                }
            }
            Log.d("ExoplayerPOC", "Player state:  $playerState")
        }
    }
}