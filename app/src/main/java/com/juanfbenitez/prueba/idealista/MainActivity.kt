package com.juanfbenitez.prueba.idealista

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.juanfbenitez.prueba.idealista.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate()/setContentView() so the splash theme
        // (Theme.PruebaIdealista.Splash) hands off cleanly to the regular app theme.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Both list and detail screens draw the idealista_primary (lime) color behind the
        // status bar, so force dark status bar icons everywhere for legibility on that light
        // background, instead of relying on the device's light/dark theme.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
