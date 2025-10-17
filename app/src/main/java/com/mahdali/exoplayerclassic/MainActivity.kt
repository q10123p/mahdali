package com.mahdali.exoplayerclassic

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private var player: ExoPlayer? = null

    private val picker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris != null && uris.isNotEmpty()) {
            val p = ensurePlayer()
            p.stop()
            p.clearMediaItems()
            for (u in uris) {
                try {
                    contentResolver.takePersistableUriPermission(
                        u,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {}
                p.addMediaItem(MediaItem.fromUri(u))
            }
            p.prepare()
            p.playWhenReady = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)

        findViewById<android.widget.Button>(R.id.btnPick).setOnClickListener {
            picker.launch(arrayOf("audio/*"))
        }
    }

    private fun ensurePlayer(): ExoPlayer {
        player?.let { return it }
        val created = ExoPlayer.Builder(this).build()
        playerView.player = created
        player = created
        return created
    }

    override fun onStart() {
        super.onStart()
        ensurePlayer()
    }

    override fun onStop() {
        super.onStop()
        playerView.player = null
        player?.release()
        player = null
    }
}
