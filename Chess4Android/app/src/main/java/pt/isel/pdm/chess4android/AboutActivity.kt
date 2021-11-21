package pt.isel.pdm.chess4android

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import pt.isel.pdm.chess4android.databinding.ActivityAboutBinding

private const val LINCHESS_URL = "https://lichess.org/api"

class AboutActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAboutBinding.inflate(layoutInflater)
    }

    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.authorsButton.setOnClickListener {
            showPopupAuthors(R.layout.authors_popup)
        }

        binding.creditsButton!!.setOnClickListener {
            showPopupCredits(R.layout.credits_popup)
        }

    }

    private fun showPopupAuthors(popupViewId: Int) {
        val alertDialog = AlertDialog.Builder(this)
        val popup = layoutInflater.inflate(popupViewId, null)

        alertDialog.setView(popup)
        dialog = alertDialog.create()
        dialog.show()
    }

    private fun showPopupCredits(popupViewId: Int) {
        val alertDialog = AlertDialog.Builder(this)
        val popup = layoutInflater.inflate(popupViewId, null)

        var apiText = popup.findViewById<TextView>(R.id.api_text)

        alertDialog.setView(popup)
        dialog = alertDialog.create()
        dialog.show()

        apiText.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(LINCHESS_URL)).apply {      //abre uma nova pagina web criando uma nova task
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }

    }

}