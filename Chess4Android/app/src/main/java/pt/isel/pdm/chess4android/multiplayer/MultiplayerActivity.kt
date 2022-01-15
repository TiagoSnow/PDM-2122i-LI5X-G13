package pt.isel.pdm.chess4android.multiplayer

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import pt.isel.pdm.chess4android.BoardClickListener
import pt.isel.pdm.chess4android.MainActivity
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.model.Army
import pt.isel.pdm.chess4android.model.PiecesType
import pt.isel.pdm.chess4android.pieces.Coord
import pt.isel.pdm.tictactoe.challenges.ChallengeInfo
import pt.isel.pdm.tictactoe.game.GameState
import pt.isel.pdm.tictactoe.game.getArmy
import pt.isel.pdm.chess4android.model.Board
import pt.isel.pdm.tictactoe.game.toGameState

private const val GAME_EXTRA = "MultiplayerActivity.GameInfoExtra"
private const val LOCAL_PLAYER_EXTRA = "MultiplayerActivity.LocalPlayerExtra"

class MultiplayerActivity : AppCompatActivity() {

    companion object {
        fun buildIntent(origin: Context, local: Army, turn: Army, challengeInfo: ChallengeInfo) =
            Intent(origin, MultiplayerActivity::class.java)
                .putExtra(GAME_EXTRA, Board(turn = turn).toGameState(challengeInfo.id, null))
                .putExtra(LOCAL_PLAYER_EXTRA, local.name)
    }

    private val localPlayer: Army? by lazy {
        val armyString = intent.getStringExtra(LOCAL_PLAYER_EXTRA)
        if (armyString != null) {
            getArmy(armyString[0])
        }
        else null
    }


    private val initialState: GameState? by lazy {
        intent.getParcelableExtra<GameState>(GAME_EXTRA)
    }


    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

//    private val viewModel: MultiplayerActivityViewModel by viewModels ()

    private val viewModel: MultiplayerActivityViewModel by viewModels {
        @Suppress("UNCHECKED_CAST")
        object: ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                Log.v("INITSTATE", "value: "+initialState!!.board)
                return MultiplayerActivityViewModel(application, initialState, localPlayer) as T
            }
        }
    }

    var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.updateOnlineCurrArmy(initialState, localPlayer)

        viewModel.beginBoard()
        binding.boardView.updateView(viewModel.gameModel.board, viewModel.gameModel.newArmyToPlay, false)

        mp = MediaPlayer.create(this, R.raw.button_pressed)
        binding.boardView.updateView(
            viewModel.gameModel.board,
            viewModel.gameModel.newArmyToPlay,
            false
        )

        val btMenuPermMul: Button = findViewById(R.id.btPermMenuMul)
        btMenuPermMul.setOnClickListener {
            mp!!.start()
            startActivity(Intent(this@MultiplayerActivity, MainActivity::class.java))
        }

        val flagSurrender: ImageView = findViewById(R.id.flag_surrender_GA)
        flagSurrender.setOnClickListener {
            mp!!.start()
            showDialogFF()
        }

        binding.boardView.setOnBoardClickedListener(listener)

        viewModel.game.observe(this) {
            it.onSuccess {

                viewModel.updateBoardFromOnline(it.board, it.turn)
                binding.boardView.updateView(viewModel.getBoard(), viewModel.getNextArmyToPlay(), false)
            }
        }

    }

    fun showDialogFF() {
        val dialog = Dialog(this@MultiplayerActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.ff_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btYesFF: MaterialButton = dialog.findViewById(R.id.btYesFF)
        btYesFF.setOnClickListener {
            mp!!.start()
            dialog.dismiss()
            viewModel.switchArmy()
            showDialog(viewModel.getNextArmyToPlay().name)
        }
        val btNoFF: MaterialButton = dialog.findViewById(R.id.btNoFF)
        btNoFF.setOnClickListener {
            mp!!.start()
            dialog.dismiss()
        }
        dialog.show()
    }

    private var listener: BoardClickListener = object : BoardClickListener {
        override fun onTileClicked(col: Int, line: Int) {
            if(viewModel.isThisPlayerTurn()) {
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
        }

        override fun onMovement(prevCoord: Coord?, newCoord: Coord?) {
            //atualizar a board
            viewModel.movePiece(prevCoord, newCoord)

            //mpM!!.start()
            //atualizar a view
            binding.boardView.movePiece(prevCoord, newCoord, viewModel.getPiece(newCoord))

            if(viewModel.verifyPiecePromotion(newCoord)) onPromotion(newCoord)

            onCheck(newCoord)
            viewModel.switchArmy()
        }

        override fun onCheck(newCoord: Coord?) {
            viewModel.gameModel.removeSignalCheck()
            val piece = viewModel.getPiece(newCoord)
            val list = piece.searchRoute()

            for (option in list)
                if (viewModel.isChecking(option)) {
                    viewModel.gameModel.signalCheck(piece, option)
                    viewModel.doubleCheck()
                    if(viewModel.isCheckMate())
                        showDialog(viewModel.getNextArmyToPlay().name)
                    break
                }

        }
    }

    private fun onPromotion(newCoord: Coord?) {
        val dialog = Dialog(this@MultiplayerActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.promotion_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val mDialogBishop: MaterialButton = dialog.findViewById(R.id.btBishop)
        mDialogBishop.setOnClickListener {
            mp!!.start()
            viewModel.promotePiece(newCoord, PiecesType.BISHOP)
            binding.boardView.updateView(viewModel.getBoard(), viewModel.getNextArmyToPlay(), false)
            dialog.dismiss()
        }

        val mDialogQueen: MaterialButton = dialog.findViewById(R.id.btQueen)
        mDialogQueen.setOnClickListener {
            mp!!.start()
            viewModel.promotePiece(newCoord, PiecesType.QUEEN)
            binding.boardView.updateView(viewModel.getBoard(), viewModel.getNextArmyToPlay(), false)
            dialog.dismiss()
        }

        val mDialogKnight: MaterialButton = dialog.findViewById(R.id.btKnight)
        mDialogKnight.setOnClickListener {
            mp!!.start()
            viewModel.promotePiece(newCoord, PiecesType.KNIGHT)
            binding.boardView.updateView(viewModel.getBoard(), viewModel.getNextArmyToPlay(), false)
            dialog.dismiss()
        }

        val mDialogRook: MaterialButton = dialog.findViewById(R.id.btRook)
        mDialogRook.setOnClickListener {
            mp!!.start()
            viewModel.promotePiece(newCoord, PiecesType.ROOK)
            binding.boardView.updateView(viewModel.getBoard(), viewModel.getNextArmyToPlay(), false)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showDialog(winner: String) {
        //passar para nova activity que mostra a mensagem a dizer checkmate
        val dialog = Dialog(this@MultiplayerActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cm_popup_multiplayer)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val text: TextView = dialog.findViewById(R.id.winnerText)
        text.text = """${getString(R.string.winnerText)} $winner!"""

        val mDialogMenu: MaterialButton = dialog.findViewById(R.id.btOk)
        mDialogMenu.setOnClickListener {
            mp!!.start()
            startActivity(Intent(this@MultiplayerActivity, MainActivity::class.java))
            dialog.dismiss()
        }

        dialog.show()
    }
}