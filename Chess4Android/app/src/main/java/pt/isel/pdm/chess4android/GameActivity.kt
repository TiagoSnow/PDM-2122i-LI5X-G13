package pt.isel.pdm.chess4android

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.button.MaterialButton
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.pieces.Coord

private const val PUZZLE_EXTRA = "PreviewPuzzleActivity.Extra.Puzzle"

class GameActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: GameActivityViewModel by viewModels()

    var mpM: MediaPlayer? = null
    var mp: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.getAllPuzzleEntity()

        if (intent.extras != null) {
            getPuzzleFromHistory()
        } else {
            getPuzzleOfDay()
        }

        binding.boardView.setOnBoardClickedListener(listener)
        mpM = MediaPlayer.create(this, R.raw.moving_piece)

        mp = MediaPlayer.create(this, R.raw.button_pressed)
        val btPermMenu: Button = findViewById(R.id.btPermMenuMul)

        btPermMenu.setOnClickListener {
            mp!!.start()
            startActivity(Intent(this@GameActivity, MainActivity::class.java))
        }

        val flag_surrender: ImageView = findViewById(R.id.flag_surrender_GA)
        flag_surrender.setOnClickListener {
            mp!!.start()
            showDialogFF()
        }
    }

    private fun getPuzzleFromHistory() {
        val puzzle = intent.extras?.get(PUZZLE_EXTRA) as PuzzleInfoDTO
        updateModel(puzzle)
    }

    private fun getPuzzleOfDay() {
        viewModel.getPuzzleOfDay()
        viewModel.dataOfDay.observe(this) {
            updateModel(viewModel.dataOfDay.value!!)
        }
        viewModel.error.observe(this) { }
    }

    private fun updateModel(puzzle: PuzzleInfoDTO) {
        viewModel.setCurrentPuzzleInfoDTO(puzzle)
        viewModel.updateBoard(puzzle.game.pgn)
        binding.boardView.updateView(
            viewModel.gameModel.board,
            viewModel.gameModel.newArmyToPlay,
            false
        )
        viewModel.updateSolutions(puzzle.puzzle.solution)
    }


    private var listener: BoardClickListener = object : BoardClickListener {
        override fun onTileClicked(col: Int, line: Int) {
            val availableSolution = viewModel.getAvailableSolution(col, line)
            if (availableSolution != null) {
                binding.boardView.paintBoard(availableSolution.col, availableSolution.line)
                binding.boardView.updateOptions(availableSolution)
            } else {
                binding.boardView.resetOptions()
            }
        }

        override fun onMovement(prevCoord: Coord?, newCoord: Coord?) {
            if (viewModel.removeSolutionSelected(Pair(prevCoord, newCoord))) {
                //atualizar a board
                viewModel.movePiece(prevCoord, newCoord)

                mpM!!.start()
                //atualizar a view
                binding.boardView.movePiece(prevCoord, newCoord, viewModel.getPiece(newCoord))

                onCheck(newCoord)
            }
        }

        override fun onCheck(newCoord: Coord?) {
            if (viewModel.gameModel.solutions.size == 0) {
                viewModel.updatePuzzleEntity()
                showDialog()
            }
        }
    }

    fun showDialog() {
        //passar para nova activity que mostra a mensagem a dizer checkmate
        val dialog = Dialog(this@GameActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cm_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val mDialogMenu: MaterialButton = dialog.findViewById(R.id.btYesFF)
        mDialogMenu.setOnClickListener {
            mp!!.start()
            startActivity(Intent(this@GameActivity, MainActivity::class.java))
            dialog.dismiss()
        }

        val mDialogReset: MaterialButton = dialog.findViewById(R.id.btNoFF)
        mDialogReset.setOnClickListener {
            mp!!.start()
            startActivity(
                buildIntent(
                    this@GameActivity,
                    viewModel.getCurrentPuzzleInfoDTO()
                )
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showDialogFF() {
        val dialog = Dialog(this@GameActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.ff_popup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btYesFF: MaterialButton = dialog.findViewById(R.id.btYesFF)
        btYesFF.setOnClickListener {
            mp!!.start()
            dialog.dismiss()
            showDialogWinner(viewModel.getNextArmyToPlay().name)
        }
        val btNoFF: MaterialButton = dialog.findViewById(R.id.btNoFF)
        btNoFF.setOnClickListener {
            mp!!.start()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDialogWinner(winner: String) {
        val dialog = Dialog(this@GameActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cm_popup_multiplayer)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val text: TextView = dialog.findViewById(R.id.winnerText)
        text.text = getString(R.string.winnerText) + " " + winner + "!"

        val mDialogMenu: MaterialButton = dialog.findViewById(R.id.btOk)
        mDialogMenu.setOnClickListener {
            mp!!.start()
            startActivity(Intent(this@GameActivity, MainActivity::class.java))
            dialog.dismiss()
        }
        dialog.show()
    }

    fun buildIntent(origin: Activity, puzzleDto: PuzzleInfoDTO): Intent {
        val puzzleDTO = Intent(origin, GameActivity::class.java)
        puzzleDTO.putExtra(PUZZLE_EXTRA, puzzleDto)
        return puzzleDTO
    }
}