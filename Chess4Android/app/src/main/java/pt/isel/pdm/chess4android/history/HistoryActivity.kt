package pt.isel.pdm.chess4android.history

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.PreviewPuzzleActivity
import pt.isel.pdm.chess4android.PuzzleInfoDTO
import pt.isel.pdm.chess4android.databinding.ActivityHistoryBinding

private const val PUZZLE_EXTRA = "PreviewPuzzleActivity.Extra.Puzzle"

class HistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HistoryActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.puzzleList.layoutManager = LinearLayoutManager(this)

        // Get the list of puzzles, if we haven't fetched it yet
        (viewModel.history ?: viewModel.loadHistory()).observe(this) {
            binding.puzzleList.adapter = HistoryAdapter(it) { puzzleDto ->
                startActivity(buildIntent(this, puzzleDto))
            }
        }
    }

    fun buildIntent(origin: Activity, puzzleDto: PuzzleInfoDTO): Intent {
        val puzzleDTO = Intent(origin, PreviewPuzzleActivity::class.java)
        puzzleDTO.putExtra(PUZZLE_EXTRA, puzzleDto)
        return puzzleDTO
    }

}