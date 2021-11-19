package pt.isel.pdm.chess4android

import android.content.Intent
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
        viewModel.getPuzzleOfDay()
        viewModel.dataOfDay.observe(this) {
            viewModel.updateBoard(
                "e4 a6 d4 h6 f4 b6 g4 c6 c4 d6 Ke2 g6 e5 dxe5 c5 e4"
            )
            binding.boardView.updateView(viewModel.board)

        }
        /*binding.boardView.setOnClickListener() {

            startActivity(Intent(this, AboutActivity::class.java))
        }*/
    }


}