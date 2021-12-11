package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        //viewModel.deletePuzzleEntity()

        viewModel.getPuzzleOfDay()

        viewModel.dataOfDay.observe(this) {
            viewModel.updateBoard(viewModel.dataOfDay.value!!.game.pgn.replace("+",""))
            binding.boardView.updateView()
            viewModel.updateSolutions(viewModel.dataOfDay.value!!.puzzle.solution)
        }
        viewModel.error.observe(this) { displayError() }

        binding.boardView.setOnBoardClickedListener(listener)
    }

    /**
     * Helper method used do display an error, if one has occured while fetching the day's quote.
     */
    private fun displayError() {
        //TODO: IMPLEMENT TOAST TO SHOW ERROR TO USER
        //Toast.makeText(this, R.string.get_quote_error, Toast.LENGTH_LONG).show()
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
           /* if(viewModel.getSolutionsSize() == 0) {
                //passar para nova activity que mostra a mesnagem a dizer checkmate
            }*/
        }
    }

}