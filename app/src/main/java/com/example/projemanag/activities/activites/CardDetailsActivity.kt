package com.example.projemanag.activities.activites

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.projemanag.R
import com.example.projemanag.activities.activites.firebase.FirestoreClass
import com.example.projemanag.activities.activites.models.Board
import com.example.projemanag.activities.activites.models.Card
import com.example.projemanag.activities.activites.models.Task
import com.example.projemanag.activities.activites.utils.Constants
import com.example.projemanag.databinding.ActivityCardDetailsBinding
import com.example.projemanag.databinding.ActivityTaskListBinding

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails:Board
    private var mTaskListPosition=-1
    private var mCardPosition=-1

    private var binding: ActivityCardDetailsBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getIntentData()
        setupActionBar()
        binding!!.etNameCardDetails.setText(mBoardDetails.TaskList[mTaskListPosition].cards[mCardPosition].name)
        binding!!.etNameCardDetails.setSelection(binding!!.etNameCardDetails.text.toString().length)

        binding!!.btnUpdateCardDetails.setOnClickListener {
            if(binding!!.etNameCardDetails.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this, "enter card name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding!!.toolbarCardDetailsActivity)

        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

            actionBar.title=mBoardDetails.TaskList[mTaskListPosition].cards[mCardPosition].name
        }

        binding!!.toolbarCardDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails=intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition=intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }

        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition=intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_delete_card->{
                deleteCard()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails(){
        val card= Card(binding!!.etNameCardDetails.text.toString(),
            mBoardDetails.TaskList[mTaskListPosition].cards[mCardPosition].createdBy,
        mBoardDetails.TaskList[mTaskListPosition].cards[mCardPosition].assignedTo)

        mBoardDetails.TaskList[mTaskListPosition].cards[mCardPosition] =card

        showProgressDialog("updating")
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList:ArrayList<Card> = mBoardDetails.TaskList[mTaskListPosition].cards

        cardsList.removeAt(mCardPosition)

        val taskList:ArrayList<Task> = mBoardDetails.TaskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards=cardsList

        showProgressDialog("deleting")
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }
}