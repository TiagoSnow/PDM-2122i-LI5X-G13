package pt.isel.pdm.chess4android

import android.content.Intent
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

        binding.creditsButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.puzzleOfDayButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

    }



}