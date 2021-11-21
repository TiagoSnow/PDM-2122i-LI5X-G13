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
            val checkOptions = viewModel.updateBoard(
                "e4 e5 h4 f6 Rh3 h6 a4 Rh7 Raa3 d6 Rhc3 Nc6 d4 Nxd4 a5 b5 Ra4 bxa4 a6 a3 h5 f5 exf5 Qh4 f6 Qh3 b3 d5 b4 Bxb4 fxg7"
            //"e4 e5 h4 f6 Rh3 h6 a4 Rh7 Raa3 d6 Rhc3 Nc6 d4 Nxd4 a5 b5 Ra4 bxa4 a6 a3 Rf3 f5 exf5 g6 h5 gxf5 Rxf5 a2 Qd3 d5 Qb5"
            )
            //"e4 e5 h4 f6 Rh3 h6 a4 Rh7 Raa3 d6 Rhc3 Nc6 d4 Nxd4 a5 b5 Ra4 bxa4 a6 a3 h5 f5 exf5 Qh4 f6 Qh3 b3 d5 b4 Bxb4 fxg7"
            binding.boardView.updateView(checkOptions)
            //d4 d5 Bf4 e6 e3 a6 Nf3 h6 Bd3 Nf6 Ne5 Bd6 Nd2 c5 c3 Qb6 Rb1 Nbd7 Ndf3 Nh5 Nxd7 Bxd7 Bxd6 Qxd6 Ne5 Nf6 O-O h5 f4 h4 Qf3 c4 Bc2 h3 g4 Ne4 Bxe4 dxe4 Qxe4 Bc6 Nxc6 bxc6 Qe5 Qe7 Qxg7 O-O-O Qe5 Qh4 Qg5 Qh7 Qc5
        }
    }


}