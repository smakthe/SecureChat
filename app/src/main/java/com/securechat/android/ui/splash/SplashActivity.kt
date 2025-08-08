package com.securechat.android.ui.splash

import android.content.Intent
// import android.graphics.drawable.AnimatedVectorDrawable // Commented out
import android.os.Bundle
import android.os.Handler
import android.os.Looper
// import android.widget.ImageView // No longer directly used here
import androidx.appcompat.app.AppCompatActivity
// import com.securechat.android.R // No longer directly used here
import com.securechat.android.databinding.ActivitySplashBinding
import com.securechat.android.ui.welcome.WelcomeActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Temporarily Commented Out Animation Code ---
        // val drawable = binding.ivLogo.drawable
        // if (drawable is AnimatedVectorDrawable) {
        //     drawable.start()
        // } else {
        //     // Log error or handle if not an AVD - needs Logcat to see this
        //     android.util.Log.e("SplashActivity", "ivLogo.drawable is not an AnimatedVectorDrawable. It is: " + (drawable?.javaClass?.name ?: "null"))
        // }
        // --- End of Commented Out Code ---

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }, 3000) // 3 seconds delay
    }
}
