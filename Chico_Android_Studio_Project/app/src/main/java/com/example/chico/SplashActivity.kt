package com.example.chico

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Black status and navigation bars
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        // Show Chico splash screen
        setContentView(R.layout.activity_splash)

        // Keep splash screen visible for 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({

            // Go to Login screen
            val intent = Intent(
                this,
                LoginActivity::class.java
            )

            startActivity(intent)

            // Prevent returning to splash screen
            finish()

        }, 2000)
    }
}