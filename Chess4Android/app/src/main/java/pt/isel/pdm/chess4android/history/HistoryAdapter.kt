package pt.isel.pdm.chess4android.history

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.PuzzleInfoDTO
import pt.isel.pdm.chess4android.R


class HistoryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val dayView = itemView.findViewById<TextView>(R.id.day)
    private val puzzleNameView = itemView.findViewById<TextView>(R.id.puzzle_name)
    private val statusView = itemView.findViewById<TextView>(R.id.solved_status)

    /**
     * Starts the item selection animation and calls [onAnimationEnd] once the animation ends
     */
    private fun startAnimation(onAnimationEnd: () -> Unit) {

        val animation = ValueAnimator.ofArgb(
            ContextCompat.getColor(itemView.context, R.color.list_item_background),
            ContextCompat.getColor(itemView.context, R.color.list_item_background_selected),
            ContextCompat.getColor(itemView.context, R.color.list_item_background)
        )

        animation.addUpdateListener { animator ->
            val background = itemView.background as GradientDrawable
            background.setColor(animator.animatedValue as Int)
        }

        animation.duration = 400
        animation.doOnEnd { onAnimationEnd() }

        animation.start()
    }

    fun bindTo(puzzleInfoDTO: PuzzleInfoDTO, action: () -> Unit) {
        dayView.text = puzzleInfoDTO.date
        puzzleNameView.text = puzzleInfoDTO.puzzle.id
        statusView.text = puzzleInfoDTO.status
        itemView.setOnClickListener {

            itemView.isClickable = false
            startAnimation {
                action()
                itemView.isClickable = true
            }
        }
    }

}

/**
 * Adapts items in a data set to RecycleView entries
 */
class HistoryAdapter(
    private val dataSource: List<PuzzleInfoDTO>,
    private val onItemCLick: (PuzzleInfoDTO) -> Unit): RecyclerView.Adapter<HistoryItemViewHolder>() {

    /**
     * Factory method of view holders (and its associated views)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_view, parent, false)
        return HistoryItemViewHolder(view)
    }

    /**
     * Associates (binds) the view associated to [viewHolder] to the item at [position] of the
     * data set to be adapted.
     */
    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bindTo(dataSource[position]) {
            onItemCLick(dataSource[position])
        }
    }

    override fun getItemCount(): Int {
        return dataSource.size
    }

}