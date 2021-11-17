package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import pt.isel.pdm.ches.GameActivityViewModel
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: GameActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.getPuzzleOfDay()

        viewModel.dataOfDay.observe(this) {
            //viewModel.dataOfDay.value?.game?.let { Raa3 d6 Rhc3 g6 Rc5 f5 Rac3 h5 R3c4
            viewModel.placePieces("e4 e5 h4 f6 Rh3 h6 a4 Rh7 Raa3")
            binding.boardView.updateView(viewModel.board)
            //viewModel.setBoard(it, binding.boardView)
        }
    }
}