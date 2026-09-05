package com.juanfbenitez.prueba.idealista

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.juanfbenitez.prueba.idealista.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Both list and detail screens draw a dark (idealista_black) scrim behind the status
        // bar, so force light (white) status bar icons everywhere instead of relying on the
        // device's light/dark theme, which would make icons invisible on a dark background.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
