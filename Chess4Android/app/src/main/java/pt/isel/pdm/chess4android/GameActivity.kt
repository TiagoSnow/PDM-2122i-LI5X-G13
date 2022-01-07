package pt.isel.pdm.chess4android

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import androidx.activity.viewModels
import com.google.android.material.button.MaterialButton
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.pieces.Coord

private const val PUZZLE_EXTRA = "PreviewPuzzleActivity.Extra.Puzzle"
const val MULTIPLAYER_EXTRA = "GameActivity.Multiplayer"


class GameActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: GameActivityViewModel by viewModels()

    var mpM: MediaPlayer? = null
    var mp: MediaPlayer? = null
    private var isMultiplayer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.extras?.get(MULTIPLAYER_EXTRA) != "Multiplayer") {
            isMultiplayer = false
            viewModel.getAllPuzzleEntity()

            if (intent.extras != null) {
                getPuzzleFromHistory()
            } else {
                getPuzzleOfDay()
            }
        } else {
            isMultiplayer = true
            viewModel.beginBoard(binding.boardView)

        }
        binding.boardView.setOnBoardClickedListener(listener)
        mpM = MediaPlayer.create(this, R.raw.moving_piece)

        mp = MediaPlayer.create(this, R.raw.button_pressed)
        val btPermMenu: Button = findViewById(R.id.btPermMenu)

        btPermMenu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                mp!!.start()
                startActivity(Intent(this@GameActivity, MainActivity::class.java))
            }
        })
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
        viewModel.error.observe(this) { displayError() }
    }

    private fun updateModel(puzzle: PuzzleInfoDTO) {
        viewModel.setCurrentPuzzleInfoDTO(puzzle)
        viewModel.updateBoard(puzzle.game.pgn.replace("+", ""))
        binding.boardView.updateView(
            viewModel.gameModel.board,
            viewModel.gameModel.newArmyToPlay,
            false
        )
        viewModel.updateSolutions(puzzle.puzzle.solution)
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
            if (!isMultiplayer) {
                val availableOption = viewModel.getAvailableOption(col, line)
                if (availableOption != null) {
                    binding.boardView.paintBoard(availableOption.col, availableOption.line)
                    binding.boardView.updateOptions(availableOption)
                } else {
                    binding.boardView.resetOptions()
                }
            } else {
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
            if (isMultiplayer ||
                !isMultiplayer && viewModel.removeOptionSelected(Pair(prevCoord, newCoord))
            ) {
                //atualizar a board
                viewModel.movePiece(prevCoord, newCoord)

                mpM!!.start()
                //atualizar a view
                binding.boardView.movePiece(prevCoord, newCoord, viewModel.getPiece(newCoord))

                onCheckmate()
            }
        }

        override fun onCheckmate() {
            if (!isMultiplayer) {

                if (viewModel.gameModel.solutions.size == 0) {

                    viewModel.updatePuzzleEntity()

                    //passar para nova activity que mostra a mesnagem a dizer checkmate
                    val dialog = Dialog(this@GameActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.cm_popup)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val mDialogMenu: MaterialButton = dialog.findViewById(R.id.btMenu)
                    mDialogMenu.setOnClickListener { //Toast.makeText(applicationContext, "Cancel", Toast.LENGTH_SHORT).show()
                        mp!!.start()
                        startActivity(Intent(this@GameActivity, MainActivity::class.java))
                        dialog.dismiss()
                    }

                    val mDialogReset: MaterialButton = dialog.findViewById(R.id.btReset)
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
            } else {
                //TODO: VER SE É CHECKMATE
            }
        }
    }

    fun buildIntent(origin: Activity, puzzleDto: PuzzleInfoDTO): Intent {
        val puzzleDTO = Intent(origin, GameActivity::class.java)
        puzzleDTO.putExtra(PUZZLE_EXTRA, puzzleDto)
        return puzzleDTO
    }
}