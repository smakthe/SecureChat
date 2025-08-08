package com.securechat.android.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.securechat.android.R
import com.securechat.android.ui.registration.RegistrationActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}