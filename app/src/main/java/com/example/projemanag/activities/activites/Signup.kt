package com.example.projemanag.activities.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.projemanag.R
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.activities.activites.models.user
import com.example.projemanag.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User

class signup : BaseActivity() {

    private var binding: ActivitySignupBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarSignupActivity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        binding?.toolbarSignupActivity?.setNavigationOnClickListener{
            onBackPressed()
        }

        binding?.btnSignup?.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser(){
        val name:String=binding!!.etName!!.text!!.toString().trim{it <= ' '}
        val email:String=binding!!.etEmail!!.text!!.toString().trim{it <= ' '}
        val password:String=binding!!.etPassowrd!!.text!!.toString().trim{it <= ' '}

        if(validateForm(name,email,password)){
            showProgressDialog("please wait...")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task!!.result!!.user!!
                    val registeredEmail = firebaseUser!!.email!!

                    val user= user(firebaseUser.uid,name,registeredEmail)
                    FirestoreClass().registerUser(this,user)
//                    finish()
                } else {
                    Toast.makeText(this, task!!.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateForm(name:String,email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("please enter name")
                false
            }
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

    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "you have succesfully registered ",
            Toast.LENGTH_LONG
        ).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }


}