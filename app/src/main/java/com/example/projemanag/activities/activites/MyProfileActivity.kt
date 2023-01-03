package com.example.projemanag.activities.activites

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.activities.activites.models.user
import com.example.projemanag.activities.activites.utils.Constants
import com.example.projemanag.databinding.ActivityMainBinding
import com.example.projemanag.databinding.ActivityMyProfileBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    private var binding: ActivityMyProfileBinding?=null



    private var mProfileImageURL:String?=null
    private lateinit var mUserDetails:user
    private var mSelectedImageUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        FirestoreClass().loadUserdata(this)

        binding!!.navProfileImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
               Constants. showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding!!.btnUpdateProfile.setOnClickListener {
            if(mSelectedImageUri!=null){
                uploadUserImage()
            }else{
                showProgressDialog("pls wait")

                updateUserProfileData()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding!!.toolbarProfileActivity)

        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        binding!!.toolbarProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUserDataInUI(user: user){

        mUserDetails=user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.nav_profile_image))

        binding!!.etNameProfile.setText(user.name)
        binding!!.etEmailProfile.setText(user.email)
        if(user.mobile!=0L){
            binding!!.etMobileProfile.setText(user.mobile.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
        }else{
            Toast.makeText(this, " you just denied permission for storage.", Toast.LENGTH_SHORT).show()

        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode==Constants.PICK_IMAGE_REQUEST_CODE && data!!.data!=null){
            mSelectedImageUri=data.data

            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(findViewById(R.id.nav_profile_image))
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String , Any>()

        if(mProfileImageURL!=null){
            if(mProfileImageURL!!.isNotEmpty() && mProfileImageURL!=mUserDetails.image){
                userHashMap[Constants.IMAGE]=mProfileImageURL!!
            }
        }
        if(binding?.etNameProfile?.text?.toString()!=mUserDetails.name){
            userHashMap[Constants.NAME]=binding?.etNameProfile?.text.toString()
        }
        if(binding?.etMobileProfile?.text?.toString()!=mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=binding?.etMobileProfile?.text?.toString()?.toLong()!!
        }

        FirestoreClass().updateUserProfileData(this,userHashMap)

    }

    private fun uploadUserImage(){
        showProgressDialog("pls wait...")
        if(mSelectedImageUri!=null){

            val sRef:StorageReference=FirebaseStorage.getInstance().reference.child("USER_IMAGE"+   System.currentTimeMillis()+"."+Constants.getFileExtention(this,mSelectedImageUri!!))

            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                taskSnaphot ->
                Log.e("Firebase Image URL",taskSnaphot.metadata!!.reference!!.downloadUrl.toString())

                taskSnaphot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mProfileImageURL=uri.toString()

                    updateUserProfileData()

                }.addOnFailureListener { 
                    exception->
                    Toast.makeText(this@MyProfileActivity, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        }
    }



     fun profileUpdateSuccess(){
         hideProgressDialog()
         setResult(Activity.RESULT_OK)
         finish()
     }
}