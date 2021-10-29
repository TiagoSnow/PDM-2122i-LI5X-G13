package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import pt.isel.pdm.chess4android.databinding.ActivityAboutBinding
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding

class AboutActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.authorsButton.setOnClickListener {
            showPopup(binding.authorsButton)
        }
    }



    private fun showPopup(view: View) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.header1 -> {
                    Toast.makeText(this@AboutActivity, item.title, Toast.LENGTH_SHORT).show()
                }
                R.id.header2 -> {
                    Toast.makeText(this@AboutActivity, item.title, Toast.LENGTH_SHORT).show()
                }
                R.id.header3 -> {
                    Toast.makeText(this@AboutActivity, item.title, Toast.LENGTH_SHORT).show()
                }
            }

            true
        })

        popup.show()
    }

}