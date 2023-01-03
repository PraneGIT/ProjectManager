package com.example.projemanag.activities.activites

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanag.R
import com.example.projemanag.activities.activites.adapters.BoardItemsAdapter
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.activities.activites.models.Board
import com.example.projemanag.activities.activites.models.user
import com.example.projemanag.activities.activites.utils.Constants
import com.example.projemanag.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {

    var fabCreateButton:FloatingActionButton?=null

    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int=11
        const val CREATE_BOARD_REQUEST_BOARD:Int=12
    }

    private lateinit var mUserName:String

    private var binding: ActivityMainBinding?=null
    private var toolbar_main:Toolbar?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        toolbar_main=findViewById(R.id.toolbar_main_activity)

        setupActionBar()

        toolbar_main!!.setNavigationOnClickListener {
            toggleDrawer()
        }

        binding!!.navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserdata(this,true)

        fabCreateButton=findViewById(R.id.fab_create_board)
        fabCreateButton?.setOnClickListener {

            val intent=Intent(this,Activity_create_board::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_BOARD)
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_main)
        toolbar_main!!.setNavigationIcon(R.drawable.ic_action_navigation_menu)
    }

    private fun toggleDrawer(){
        if(binding!!.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding!!.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            binding!!.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding!!.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding!!.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_my_profile ->{
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

        }
        binding!!.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user:user,readBoardsList:Boolean){

        mUserName=user.name

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.nav_user_image))
        findViewById<TextView>(R.id.tv_username).text = user.name

        if(readBoardsList){
            showProgressDialog("pls wait")
            FirestoreClass().getBoardsList(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserdata(this)
        }else if(resultCode==Activity.RESULT_OK && requestCode== CREATE_BOARD_REQUEST_BOARD){
            FirestoreClass().getBoardsList(this)
        } else{
            Log.e("Cancelled","Cancelled ho gya ho")
        }
    }

    fun populateBoardListToUI(boardsList:ArrayList<Board>){

        hideProgressDialog()
        if(boardsList.size>0){
            findViewById<RecyclerView>(R.id.rv_board_list).visibility= View.VISIBLE
            findViewById<TextView>(R.id.tv_no_boards).visibility=View.GONE

            findViewById<RecyclerView>(R.id.rv_board_list).layoutManager=LinearLayoutManager(this)
            findViewById<RecyclerView>(R.id.rv_board_list).hasFixedSize()

            val adapter=BoardItemsAdapter(this,boardsList)
            findViewById<RecyclerView>(R.id.rv_board_list).adapter=adapter

            adapter.setOnClickListener(object :BoardItemsAdapter.OnclickListener{
                override fun onclick(position: Int, model: Board) {
                    val intent=Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            findViewById<RecyclerView>(R.id.rv_board_list).visibility= View.GONE
            findViewById<TextView>(R.id.tv_no_boards).visibility=View.VISIBLE
        }
    }
}