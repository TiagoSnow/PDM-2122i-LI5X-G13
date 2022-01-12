package pt.isel.pdm.chess4android.multiplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import pt.isel.pdm.chess4android.BoardClickListener
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.pieces.Coord

class MultiplayerActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: MultiplayerActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.beginBoard(binding.boardView)

        binding.boardView.setOnBoardClickedListener(listener)
    }


    private var listener: BoardClickListener = object : BoardClickListener {
        //TODO FIX BUG: Clicar em peça, clicar em sitio que não é route e depois clicar em sitio de route crasha
        override fun onTileClicked(col: Int, line: Int) {
            if (viewModel.gameModel.board[col][line] != null &&
                viewModel.getNextArmyToPlay() == viewModel.currPieceArmy(col, line)
            ) {
                val availableOptions = viewModel.getAllOptions(col, line)
                if (availableOptions != null) {
                    for (option in availableOptions) {
                        if (option != null) {
                            binding.boardView.paintBoard(option.first.col, option.first.line)
                            binding.boardView.updateOptions(option.first)
                        }
                    }
                } else {
                    binding.boardView.resetOptions()
                }
            }
        }

        override fun onMovement(prevCoord: Coord?, newCoord: Coord?) {
            //atualizar a board
            viewModel.movePiece(prevCoord, newCoord)

            //mpM!!.start()
            //atualizar a view
            binding.boardView.movePiece(prevCoord, newCoord, viewModel.getPiece(newCoord))
            onCheck(newCoord)
            viewModel.switchArmy()


            /*if (viewModel.removeOptionSelected(Pair(prevCoord, newCoord))) {
                //atualizar a board
                viewModel.movePiece(prevCoord, newCoord)

                //mpM!!.start()
                //atualizar a view
                binding.boardView.movePiece(prevCoord, newCoord, viewModel.getPiece(newCoord))

                onCheckmate()
            }*/
        }

        override fun onCheck(newCoord: Coord?) {
            viewModel.gameModel.removeSignalCheck()
            val piece = viewModel.getPiece(newCoord)
            val list = piece.searchRoute()

            for (option in list)
                if (viewModel.isChecking(option)) {
                    viewModel.gameModel.signalCheck(piece, option)
                    break
                }
        }
    }
}