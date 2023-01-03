package com.example.projemanag.activities.activites

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeFace:Typeface=Typeface.createFromAsset(assets,"Prototype.ttf")
        binding?.tvAppName?.typeface=typeFace

        Handler().postDelayed({

            var currentUserId=FirestoreClass().getCurrentUSerId()
            if(currentUserId.isNotEmpty()){
                startActivity(Intent(this,MainActivity::class.java))
            }
            else{
                startActivity(Intent(this, IntroActivity::class.java))
            }

            finish()
        },2500)
    }
}