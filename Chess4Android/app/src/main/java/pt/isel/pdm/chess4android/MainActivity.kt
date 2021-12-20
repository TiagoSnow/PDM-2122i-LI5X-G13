package pt.isel.pdm.chess4android

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isel.pdm.chess4android.about.AboutActivity
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding
import pt.isel.pdm.chess4android.history.HistoryActivity
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import java.util.*


const val FLAG: String = "Idiom_flag"

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var idiom = "_english"
    private var flag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intent = intent
        flag = intent.getIntExtra(FLAG, 0)

        idiomInit()

        val  mp: MediaPlayer = MediaPlayer.create(this, R.raw.button_pressed)

        binding.creditsButton.setOnClickListener {
            mp.start()
            startActivity(Intent(this, AboutActivity::class.java))
        }

        binding.puzzleOfDayButton.setOnClickListener {
            mp.start()
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.historyButton.setOnClickListener {
            mp.start()
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.imageButton.setOnClickListener {
            changeLanguage()
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun idiomInit() {
        if (flag == 0) {
            val drawable = getDrawable(R.mipmap.united_kingdom)
            binding.imageButton.foreground = drawable
            setAppLocale("en")
            idiom = "_english"
        } else if (flag == 1) {
            val drawable = getDrawable(R.mipmap.portugal_flag_foreground)
            binding.imageButton.foreground = drawable
            setAppLocale("pt")
            idiom = "_portuguese"
        }
    }

    private fun changeLanguage() {
        if (idiom == "_portuguese") {
            idiom = "_english"
            val drawable = getDrawable(R.mipmap.united_kingdom)
            binding.imageButton.foreground = drawable
            binding.imageButton.invalidateDrawable(drawable!!)
            setAppLocale("en")
            flag = 0
            startMain()
        } else {
            idiom = "_portuguese"
            val drawable = getDrawable(R.mipmap.portugal_flag_foreground)
            binding.imageButton.foreground = drawable
            binding.imageButton.invalidateDrawable(drawable!!)
            setAppLocale("pt")
            flag = 1
            startMain()
        }
    }

    private fun setAppLocale(localeCode: String) {
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.setLocale(Locale(localeCode))
        res.updateConfiguration(conf, dm)
    }

    private fun startMain() {
        val main = Intent(this, MainActivity::class.java)
        main.putExtra(FLAG, flag)
        startActivity(main)
    }

}