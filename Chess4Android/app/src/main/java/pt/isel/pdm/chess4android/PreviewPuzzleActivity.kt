package pt.isel.pdm.chess4android

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import pt.isel.pdm.chess4android.databinding.ActivityPreviewPuzzleBinding
import pt.isel.pdm.chess4android.history.HistoryActivity

private const val PUZZLE_EXTRA = "PreviewPuzzleActivity.Extra.Puzzle"

class PreviewPuzzleActivity : AppCompatActivity() {

    companion object {
        var active = false
    }

    override fun onStart() {
        super.onStart()
        active = true
    }

    override fun onStop() {
        super.onStop()
        active = false
    }

    private val binding by lazy {
        ActivityPreviewPuzzleBinding.inflate(layoutInflater)
    }

    private val viewModel: PreviewPuzzleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val puzzle = intent.extras?.get(PUZZLE_EXTRA) as PuzzleInfoDTO
        viewModel.updateBoard(puzzle.game.pgn.replace("+", ""))
        binding.boardViewPreview.updateView(
            viewModel.gameModel.board,
            viewModel.gameModel.newArmyToPlay,
            true
        )

        val btPrevPlay: Button = findViewById(R.id.btPrevPlay)
        btPrevPlay.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //startActivity(Intent(this@PreviewPuzzleActivity, GameActivity::class.java))
                startActivity(buildIntent(this@PreviewPuzzleActivity, puzzle))
            }
        })

        val btPrevBack: Button = findViewById(R.id.btPrevBack)
        btPrevBack.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(this@PreviewPuzzleActivity, HistoryActivity::class.java))
            }

        })

        val btPrevMenu: Button = findViewById(R.id.btPrevMenu)
        btPrevMenu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(this@PreviewPuzzleActivity, MainActivity::class.java))

            }

        })

    }

    fun buildIntent(origin: Activity, puzzleDto: PuzzleInfoDTO): Intent {
        val puzzleDTO = Intent(origin, GameActivity::class.java)
        puzzleDTO.putExtra(PUZZLE_EXTRA, puzzleDto)
        return puzzleDTO
    }

}
