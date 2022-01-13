package pt.isel.pdm.chess4android.multiplayer

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.activity.viewModels
import com.google.android.material.button.MaterialButton
import pt.isel.pdm.chess4android.BoardClickListener
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.model.PiecesType
import pt.isel.pdm.chess4android.pieces.Coord

class MultiplayerActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: MultiplayerActivityViewModel by viewModels()
    var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.beginBoard()
        binding.boardView.updateView(viewModel.gameModel.board, viewModel.gameModel.newArmyToPlay, false)

        mp = MediaPlayer.create(this, R.raw.button_pressed)
        binding.boardView.updateView(
            viewModel.gameModel.board,
            viewModel.gameModel.newArmyToPlay,
            false
        )

        binding.boardView.setOnBoardClickedListener(listener)
    }


    private var listener: BoardClickListener = object : BoardClickListener {
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

            //popDaPromotion
            if(viewModel.verifyPiecePromotion(newCoord)){
                val dialog = Dialog(this@MultiplayerActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.promotion_popup)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val mDialogBishop: MaterialButton = dialog.findViewById(R.id.btBishop)
                mDialogBishop.setOnClickListener {
                    mp!!.start()
                    viewModel.promotePiece(newCoord,PiecesType.BISHOP)
                    binding.boardView.updateView(viewModel.getBoard(),viewModel.getNextArmyToPlay(),false)
                    dialog.dismiss()
                }

                val mDialogQueen: MaterialButton = dialog.findViewById(R.id.btQueen)
                mDialogQueen.setOnClickListener {
                    mp!!.start()
                    viewModel.promotePiece(newCoord,PiecesType.QUEEN)
                    binding.boardView.updateView(viewModel.getBoard(),viewModel.getNextArmyToPlay(),false)
                    dialog.dismiss()
                }

                val mDialogKnight: MaterialButton = dialog.findViewById(R.id.btKnight)
                mDialogKnight.setOnClickListener {
                    mp!!.start()
                    viewModel.promotePiece(newCoord,PiecesType.KNIGHT)
                    binding.boardView.updateView(viewModel.getBoard(),viewModel.getNextArmyToPlay(),false)
                    dialog.dismiss()
                }

                val mDialogRook: MaterialButton = dialog.findViewById(R.id.btRook)
                mDialogRook.setOnClickListener {
                    mp!!.start()
                    viewModel.promotePiece(newCoord,PiecesType.ROOK)
                    binding.boardView.updateView(viewModel.getBoard(),viewModel.getNextArmyToPlay(),false)
                    dialog.dismiss()
                }

                dialog.show()
            }

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

            viewModel.doubleCheck()

        }
    }
}