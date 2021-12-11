package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding

private const val PUZZLE_EXTRA = "PreviewPuzzleActivity.Extra.Puzzle"

class PreviewPuzzleActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}