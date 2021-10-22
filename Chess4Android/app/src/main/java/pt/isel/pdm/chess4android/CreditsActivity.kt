package pt.isel.pdm.chess4android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isel.pdm.chess4android.databinding.ActivityCreditsBinding
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding

class CreditsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCreditsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //setContentView(R.layout.activity_credits)
    }
}