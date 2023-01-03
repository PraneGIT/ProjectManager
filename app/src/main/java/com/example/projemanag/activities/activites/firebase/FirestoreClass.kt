package com.example.projemanag.activities.activites.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projemanag.activities.activites.*
import com.example.projemanag.activities.activites.models.Board
import com.example.projemanag.activities.activites.models.user
import com.example.projemanag.activities.activites.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User

class FirestoreClass {

    private val mFireStore=FirebaseFirestore.getInstance()

    fun registerUser(activity:signup,userinfo:user){
        mFireStore.collection(Constants.USERS).document(getCurrentUSerId()).set(userinfo,
            SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
        }.addOnFailureListener{
            e->
            Log.e(activity.javaClass.simpleName,"Error")
        }
    }

    fun createBoard(activity:Activity_create_board,board: Board){
        mFireStore.collection((Constants.BOARDS)).document().set(board, SetOptions.merge()).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"BOARD created successfully")
            Toast.makeText(activity, "BOARD created successfully", Toast.LENGTH_SHORT).show()
            activity.boardCreatedSuccessfully()
        }.addOnFailureListener {
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error in board",e)
        }
    }

    fun getCurrentUSerId():String{

        var currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if(currentUser!=null){
            currentUserId=currentUser.uid
        }
        return currentUserId
    }

    fun loadUserdata(activity: Activity,readBoardsList:Boolean=false){
        mFireStore.collection(Constants.USERS).document(getCurrentUSerId()).get().addOnSuccessListener {document->
            val loggedInUser=document.toObject(user::class.java)

            when(activity){
                is SignIn ->{
                    if (loggedInUser != null) {
                        activity.signInSuccess(loggedInUser)
                    }
                }
                is MainActivity->{
                    activity.updateNavigationUserDetails(loggedInUser!!,readBoardsList)
                }
                is MyProfileActivity ->{
                    activity.setUserDataInUI(loggedInUser!!)
                }
            }


        }.addOnFailureListener{
            e->

            when(activity){
                is SignIn ->{
                    activity.hideProgressDialog()
                }
                is MainActivity->{
                    activity.hideProgressDialog()
                }
            }
            Log.e(activity.javaClass.simpleName,"Error")
        }

    }

    fun addUpdateTaskList(activity: Activity,board: Board){
        val taskListHashMap = HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST]=board.TaskList

        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashMap).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"Task list updates ")
            if(activity is TaskListActivity){
                activity.addUpdateTaskListSuccess()
            }
            else if(activity is CardDetailsActivity){
                activity.addUpdateTaskListSuccess()
            }

        }.addOnFailureListener {
                e->
            if(activity is TaskListActivity){
                activity.hideProgressDialog()
            }
            else if(activity is CardDetailsActivity){
                activity.hideProgressDialog()
            }

            Log.e(activity.javaClass.simpleName,"Error while creating a board",e)
        }
    }

    fun updateUserProfileData(activity:MyProfileActivity,userHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUSerId()).update(userHashMap).addOnSuccessListener {
            Log.e(activity.javaClass.simpleName,"Profile Data updated")
            Toast.makeText(activity, "profile updated successfully", Toast.LENGTH_SHORT).show()
            activity.profileUpdateSuccess()
        }.addOnFailureListener {
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating a board")
        }
    }

    fun getBoardsList(activity:MainActivity){
        mFireStore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO,getCurrentUSerId()).get().addOnSuccessListener {
            document->
            Log.i(activity.javaClass.simpleName,document.documents.toString())
            val boardsList:ArrayList<Board> = ArrayList()
            for(i in document){
                val board=i.toObject(Board::class.java)
                board.documentId=i.id
                boardsList.add(board)
            }
            activity.populateBoardListToUI(boardsList)
        }.addOnFailureListener {
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while populating a board")
        }
    }

    fun getBoardDetails(activity: TaskListActivity, boardDocumentId: String) {
        mFireStore.collection(Constants.BOARDS).document(boardDocumentId).get().addOnSuccessListener {
                document->
            Log.i(activity.javaClass.simpleName,document.toString())
            val board=document.toObject(Board::class.java)!!
            board.documentId=document.id
            activity.boardDetails(board)
        }.addOnFailureListener {
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while populating a board")
        }
    }

    fun getMembersAssignedMembersListDetails(activity: MembersActivity,assignedTo:ArrayList<String>){
        mFireStore.collection(Constants.USERS).whereIn(Constants.ID,assignedTo).get().addOnSuccessListener {
            document->
            Log.e(activity.javaClass.simpleName,document.toString())

            val userList:ArrayList<user> =ArrayList()

            for(i in document.documents){
                val users=i.toObject(user::class.java)
                userList.add(users!!)
            }

            activity.setupMembersList(userList)
        }.addOnFailureListener {
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error members")
        }
    }

    fun getMemberDetails(activity: MembersActivity,email:String){
        mFireStore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL,email).get().addOnSuccessListener {
            document->
            if(document.size()>0){
                val user=document.documents[0].toObject(user::class.java)
                activity.memberDetails(user!!)
            }else{
                activity.hideProgressDialog()
                activity.showProgressDialog("No such member found")
            }
        }.addOnFailureListener {
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"error while members")
        }

    }

    fun assignMemberToBoard(activity: MembersActivity,board: Board,user: user){

        val assignedToHashMap= HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO]=board.assignedTo
        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(assignedToHashMap).addOnSuccessListener {
            activity.memberAssignSuccess(user)
        }.addOnFailureListener {
                e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"error while members")
        }
    }

}