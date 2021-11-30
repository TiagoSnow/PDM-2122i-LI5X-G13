package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.pieces.Coord

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
            val checkOptions = viewModel.updateBoard(viewModel.dataOfDay.value!!.game.pgn.replace("+",""))
            binding.boardView.updateView(checkOptions)
            viewModel.updateSolutions(viewModel.dataOfDay.value!!.puzzle.solution)
        }
        binding.boardView.setOnBoardClickedListener(listener)
    }

    private var listener: BoardClickListener = object : BoardClickListener {
        override fun onTileClicked(col: Int, line: Int) {
            val availableOption = viewModel.getAvailableOption(col, line)
            if(availableOption != null){
                binding.boardView.paintBoard(availableOption.col, availableOption.line)
                binding.boardView.updateOptions(availableOption)
            }
        }

        override fun onMovement(prevCoord: Coord?, newCoord: Coord?) {
            //atualizar a board
            viewModel.movePiece(prevCoord, newCoord)

            //atualizar a view
            binding.boardView.movePiece(prevCoord, newCoord, viewModel.getPiece(newCoord))

            onCheckmate()
        }

        override fun onCheckmate() {
            if(viewModel.getSolutionsSize() == 0) {
                //passar para nova activity que mostra a mesnagem a dizer checkmate
            }
        }
    }

}