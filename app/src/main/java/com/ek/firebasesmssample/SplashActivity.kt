package com.ek.firebasesmssample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val textView = findViewById<TextView>(R.id.text_view)
        val imageView = findViewById<ImageView>(R.id.image_view)

        textView.text = "Firebase Sms Sample"
        // imageView.setImageResource(R.drawable.your_image) // Resminizi buraya ekleyin

        sharedPrefManager = SharedPrefManager(this)

        // UID değerini oku
        val uid = sharedPrefManager.getUID()

        Handler(Looper.getMainLooper()).postDelayed({
            var intent = Intent(this, MainActivity::class.java)
            if(uid == null){
                intent = Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000) // 2 saniye sonra MainActivity'e geçiş yap
    }
}