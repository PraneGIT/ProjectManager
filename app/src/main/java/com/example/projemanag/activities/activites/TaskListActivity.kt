package com.example.projemanag.activities.activites

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projemanag.R
import com.example.projemanag.activities.activites.adapters.TaskListItemAdapter
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.activities.activites.models.Board
import com.example.projemanag.activities.activites.models.Card
import com.example.projemanag.activities.activites.models.Task
import com.example.projemanag.activities.activites.utils.Constants
import com.example.projemanag.databinding.ActivityMainBinding
import com.example.projemanag.databinding.ActivityMyProfileBinding
import com.example.projemanag.databinding.ActivityTaskListBinding
import java.text.FieldPosition

class TaskListActivity : BaseActivity() {

    private lateinit var mBoardDetails:Board
    private lateinit var mBoardDocumentID:String

    private var binding: ActivityTaskListBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentID=intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog("pls wait")
        FirestoreClass().getBoardDetails(this,mBoardDocumentID)

    }

    private fun setupActionBar(){
        setSupportActionBar(binding!!.toolbarTaskListActivity)

        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            actionBar.title=mBoardDetails.name
        }
        binding!!.toolbarTaskListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun boardDetails(board: Board){

        mBoardDetails=board

        hideProgressDialog()
        setupActionBar()

        val addTaskList= Task("Add List")
        board.TaskList.add(addTaskList)

        binding!!.rvTaskList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding!!.rvTaskList.setHasFixedSize(true)
        val adapter=TaskListItemAdapter(this,board.TaskList)
        binding!!.rvTaskList.adapter=adapter
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog("wait..")
        FirestoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    fun createTaskList(taskListName:String){
        val task=Task(taskListName,FirestoreClass().getCurrentUSerId())

        mBoardDetails.TaskList.add(0,task)
        mBoardDetails.TaskList.removeAt(mBoardDetails.TaskList.size-1)

        showProgressDialog("creating...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun updateTaskList(position:Int,listName:String,model:Task){
        val task=Task(listName,model.createdBy)
        mBoardDetails.TaskList[position]=task
        mBoardDetails.TaskList.removeAt(mBoardDetails.TaskList.size-1)
        showProgressDialog("updating")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position:Int){
        mBoardDetails.TaskList.removeAt(position)
        mBoardDetails.TaskList.removeAt(mBoardDetails.TaskList.size-1)
        showProgressDialog("deleting")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position:Int,cardName:String){
        mBoardDetails.TaskList.removeAt(mBoardDetails.TaskList.size-1)

        val cardAssignedUserList:ArrayList<String> =ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUSerId())

        val card=Card(cardName,FirestoreClass().getCurrentUSerId(),cardAssignedUserList)

        val cardsList=mBoardDetails.TaskList[position].cards
        cardsList.add(card)

        val task=Task(
            mBoardDetails.TaskList[position].title,
            mBoardDetails.TaskList[position].createdBy,
            cardsList
        )

        mBoardDetails.TaskList[position]=task

        showProgressDialog("pls wait...")
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members ->{
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    companion object{
        const val MEMBERS_REQUEST_CODE:Int=13
        const val CARD_DETAILS_REQUEST_CODE:Int=14
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== MEMBERS_REQUEST_CODE || requestCode== CARD_DETAILS_REQUEST_CODE){
            showProgressDialog("pls wait")
            FirestoreClass().getBoardDetails(this,mBoardDocumentID)
        }
        else{
            Log.e("Cancelled","cancelled ho gya")
        }
    }

    fun cardDetails(taskListPosition:Int,cardPosition:Int){
        val intent=Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        startActivityForResult(intent, CARD_DETAILS_REQUEST_CODE)
    }
}