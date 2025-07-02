package com.example.testapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


@SuppressLint("CustomSplashScreen") // Optional: suppress warning
class SplashActivity : AppCompatActivity() {

    private lateinit var logo: ImageView
    private lateinit var name: TextView
    private lateinit var own1: TextView
    private lateinit var own2: TextView
    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        // Find views
        logo = findViewById(R.id.logoimg)
        name = findViewById(R.id.logonameimg)
        own1 = findViewById(R.id.ownone)
        own2 = findViewById(R.id.owntwo)

        // Load animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        // Apply animations
        logo.startAnimation(topAnim)
        name.startAnimation(bottomAnim)
        own1.startAnimation(bottomAnim)
        own2.startAnimation(bottomAnim)

        // Delay and move to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)
    }
}