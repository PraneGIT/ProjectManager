package com.example.projemanag.activities.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.projemanag.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private var binding: ActivityIntroBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding?.btnSignup?.setOnClickListener {
            startActivity(Intent(this, signup::class.java))
        }

        binding?.btnSignin?.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }
    }
}