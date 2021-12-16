package pt.isel.pdm.chess4android

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.button.MaterialButton
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import pt.isel.pdm.chess4android.history.HistoryActivity
import pt.isel.pdm.chess4android.pieces.Coord

class GameActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: GameActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //viewModel.deletePuzzleEntity()

        viewModel.getPuzzleOfDay()


        //Quando se entrar na GameActivity temos de ver se veio da MainActivity ou da HistoryActivity???
        viewModel.dataOfDay.observe(this) {
            viewModel.updateBoard(viewModel.dataOfDay.value!!.game.pgn.replace("+", ""))
            binding.boardView.updateView(
                viewModel.gameModel.board,
                viewModel.gameModel.newArmyToPlay,
                false
            )
            viewModel.updateSolutions(viewModel.dataOfDay.value!!.puzzle.solution)
        }
        viewModel.error.observe(this) { displayError() }

        binding.boardView.setOnBoardClickedListener(listener)

        val btPermMenu: Button = findViewById(R.id.btPermMenu)
        btPermMenu.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                startActivity(Intent(this@GameActivity, MainActivity::class.java))
            }
        })
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
            //viewModel.getSolutionsSize(viewModel.dataOfDay.value?.puzzle?.solution!!) == 0
            if (viewModel.gameModel.solutions.size == 0) {
                //passar para nova activity que mostra a mesnagem a dizer checkmate
                val dialog = Dialog(this@GameActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.cm_popup)
                dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val mDialogMenu: MaterialButton = dialog.findViewById(R.id.btMenu)
                mDialogMenu.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        //Toast.makeText(applicationContext, "Cancel", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@GameActivity, MainActivity::class.java))
                        dialog.dismiss()
                    }

                })

                val mDialogReset: MaterialButton = dialog.findViewById(R.id.btReset)
                mDialogReset.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        startActivity(Intent(this@GameActivity, GameActivity::class.java))
                        dialog.dismiss()
                    }
                })

                dialog.show()
            }
        }


    }

}