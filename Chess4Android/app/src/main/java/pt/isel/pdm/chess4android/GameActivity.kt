package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: GameActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.boardView.setup(viewModel.gameModel)
        viewModel.getPuzzleOfDay()
        viewModel.dataOfDay.observe(this) {
            val checkOptions = viewModel.updateBoard(viewModel.dataOfDay.value!!.game.pgn)
            binding.boardView.updateView(checkOptions)
        }
    }


}