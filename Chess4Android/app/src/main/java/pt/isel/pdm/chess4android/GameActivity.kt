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
                "e4 e5 h4 f6 Rh3 h6 a4 Rh7 Raa3 d6 Rhc3 Nc6 d4 Nxd4"
            )
            binding.boardView.updateView(viewModel.board)

        }
        /*binding.boardView.setOnClickListener() {

            startActivity(Intent(this, AboutActivity::class.java))
        }*/
    }


}