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
            //viewModel.dataOfDay.value?.game?.let {
            viewModel.placePieces("h4 a6 Nh3 Na7")
            binding.boardView.updateView(viewModel.board)
            //viewModel.setBoard(it, binding.boardView)
        }
    }
}