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
        override fun onTileClicked(col: Int, line: Int) {
            if(viewModel.getNextArmyToPlay() == viewModel.currPieceArmy(col, line)) {
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

            viewModel.switchArmy()

            onCheckmate(newCoord)
            /*if (viewModel.removeOptionSelected(Pair(prevCoord, newCoord))) {
                //atualizar a board
                viewModel.movePiece(prevCoord, newCoord)

                //mpM!!.start()
                //atualizar a view
                binding.boardView.movePiece(prevCoord, newCoord, viewModel.getPiece(newCoord))

                onCheckmate()
            }*/
        }

        override fun onCheckmate(currCoord: Coord?) {
            //TODO: VER SE É CHECKMATE

            var piece = viewModel.getPiece(currCoord)
            var list = piece.searchRoute()

            for(option in list) {
                if(optionIsKing()){
                    viewModel.gameModel.isChecking = true
                }
            }

            /*
            chamar check()
            se tiver em check -> ver se estou em posição de checkMate
             */
        }
    }
}