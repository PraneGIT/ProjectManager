package com.example.projemanag.activities.activites

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projemanag.R
import com.example.projemanag.activities.activites.adapters.MemberListItemsAdapter
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.activities.activites.models.Board
import com.example.projemanag.activities.activites.models.user
import com.example.projemanag.activities.activites.utils.Constants
import com.example.projemanag.databinding.ActivityMembersBinding
import com.example.projemanag.databinding.ActivityTaskListBinding

class MembersActivity : BaseActivity() {

    private var anyChangesMade:Boolean=false
    private lateinit var mBoardDetails:Board
    private lateinit var mAssignedMembersList:ArrayList<user>
    private var binding: ActivityMembersBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails=intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!

            showProgressDialog("loading members...")
            FirestoreClass().getMembersAssignedMembersListDetails(this,mBoardDetails.assignedTo)
        }
        setupActionBar()

    }

    private fun setupActionBar(){
        setSupportActionBar(binding!!.toolbarMembersActivity)

        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title="Members"
        }
        binding!!.toolbarMembersActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun memberDetails(user:user){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetails,user)
    }

    fun setupMembersList(list:ArrayList<user>){
        hideProgressDialog()

        mAssignedMembersList=list
        binding?.rvMembersList?.layoutManager=LinearLayoutManager(this)
        binding?.rvMembersList?.hasFixedSize()
        val adapter=MemberListItemsAdapter(this,list)
        binding?.rvMembersList?.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    private fun dialogSearchMember(){
        val dialog=Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener{
                val email=dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog("pls wait")
                FirestoreClass().getMemberDetails(this,email)
            }else{
                Toast.makeText(this@MembersActivity, "enter email address", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberAssignSuccess(user:user){
        hideProgressDialog()
        mAssignedMembersList.add(user)
        setupMembersList(mAssignedMembersList)
        anyChangesMade=true
    }
}