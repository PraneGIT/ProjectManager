package com.example.projemanag.activities.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.projemanag.R
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.activities.activites.models.user
import com.example.projemanag.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase

class SignIn : BaseActivity() {
    private var binding: ActivitySignInBinding?=null
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        auth = FirebaseAuth.getInstance()

        binding?.btnSignin?.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSigninActivity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        binding?.toolbarSigninActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun signInRegisteredUser(){
        val email:String=binding!!.etEmail!!.text!!.toString().trim{it <= ' '}
        val password:String=binding!!.etPassowrd!!.text!!.toString().trim{it <= ' '}

        if(validateForm(email, password)){
            showProgressDialog("please wait...")

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        FirestoreClass().loadUserdata(this)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Sign in", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("please enter email")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("please enter password")
                false
            }else ->{
                true;
            }
        }
    }

    fun signInSuccess(user:user){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}