package com.example.projemanag.activities.activites

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.activities.activites.models.Board
import com.example.projemanag.activities.activites.utils.Constants
import com.example.projemanag.databinding.ActivityCreateBoardBinding
import com.example.projemanag.databinding.ActivityMyProfileBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class Activity_create_board : BaseActivity() {

    private var binding: ActivityCreateBoardBinding?=null
    private  var mSelectedImageUri: Uri?=null

    private var mBoardImageUrl:String=""
    private lateinit var mUserName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName=intent.getStringExtra(Constants.NAME)!!
        }

        binding!!.ivCreateBoard!!.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                Constants. showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding!!.btnCreateBoard.setOnClickListener {
            if(mSelectedImageUri!=null){
                uploadBoardImage()
            }else{
                showProgressDialog("pls wait")
                CreateBoard()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding!!.toolbarCreateBoard)

        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }
        binding!!.toolbarCreateBoard.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun  CreateBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())

        var board= Board(
            binding!!.etBoard.text.toString(),
            mBoardImageUrl,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this,board)
    }

    private fun uploadBoardImage(){
        showProgressDialog("pls wait")

        if(mSelectedImageUri!=null){

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("BOARD_IMAGE"+   System.currentTimeMillis()+"."+Constants.getFileExtention(this,mSelectedImageUri!!))

            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                    taskSnaphot ->
                Log.e("FirebaseBoardImage URL",taskSnaphot.metadata!!.reference!!.downloadUrl.toString())

                taskSnaphot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri->
                    Log.i("Downloadable Image URL",uri.toString())
                    mBoardImageUrl=uri.toString()

                   CreateBoard()

                }.addOnFailureListener {
                        exception->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }
        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
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
        if(resultCode== Activity.RESULT_OK && requestCode== Constants.PICK_IMAGE_REQUEST_CODE && data!!.data!=null){
            mSelectedImageUri=data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(findViewById(R.id.iv_CreateBoard))
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}