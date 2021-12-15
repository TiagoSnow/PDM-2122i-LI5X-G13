package pt.isel.pdm.chess4android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.databinding.ActivityPreviewPuzzleBinding

private const val PUZZLE_EXTRA = "PreviewPuzzleActivity.Extra.Puzzle"

class PreviewPuzzleActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPreviewPuzzleBinding.inflate(layoutInflater)
    }
    private val viewModel: PreviewPuzzleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val puzzle = intent.extras?.get(PUZZLE_EXTRA) as PuzzleInfoDTO
        viewModel.updateBoard(puzzle.game.pgn.replace("+",""))
        binding.boardViewPreview.updateView()
    }
}