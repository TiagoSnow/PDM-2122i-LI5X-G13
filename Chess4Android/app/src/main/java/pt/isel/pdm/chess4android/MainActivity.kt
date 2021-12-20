package pt.isel.pdm.chess4android

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isel.pdm.chess4android.about.AboutActivity
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import pt.isel.pdm.chess4android.history.HistoryActivity

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val svc = Intent(this, BackgroundSoundService::class.java)
        startService(svc)

        val  mp: MediaPlayer = MediaPlayer.create(this, R.raw.button_pressed)

        binding.creditsButton.setOnClickListener {
            mp.start()
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.puzzleOfDayButton.setOnClickListener {
            mp.start()
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.historyButton.setOnClickListener {
            mp.start()
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.imageButton.setOnClickListener {

        }

    }
}