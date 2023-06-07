package com.psut.mytutor

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminActivity : AppCompatActivity(), DeleteUserButton {

    private var usersRv: RecyclerView? = null
    private lateinit var adapter: AdminHomePageUsersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        usersRv = findViewById(R.id.usersRv)

        fetchAllUsersFromDatabase()
    }

    private fun fetchAllUsersFromDatabase() {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val users = mutableListOf<UserData>()
                    snapshot.children.forEach{user->
                        val userData: UserData = user.getValue(UserData::class.java)!!
                        users.add(userData)
                    }
                    showUsersList(users)
                }else{
                    showDialog("There is no registered users")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showDialog(error.message /*Built in message from Firebase*/)
            }
        })
    }

    private fun showUsersList(users: MutableList<UserData>) {
        adapter = AdminHomePageUsersAdapter(users, this)
        usersRv?.setHasFixedSize(true)
        usersRv?.layoutManager = LinearLayoutManager(this)
        usersRv?.adapter = adapter
    }

    private fun showDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Error")
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK") { dialog, which ->
            // Do something when the "OK" button is clicked
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun onDeleteUserButtonClicked(userUID: String) {
        val blockedUsersDatabaseRef = FirebaseDatabase.getInstance().reference.child("blocked_users").child(userUID)
        blockedUsersDatabaseRef.setValue(userUID).addOnCompleteListener { blockTask->
            if(blockTask.isSuccessful){
                // The user has been blocked
                showDialog("User has been deleted")
                val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userUID)
                databaseRef.removeValue().addOnCompleteListener { task->
                    if(task.isSuccessful){
                        // The user record deleted successfully
                        fetchAllUsersFromDatabase()
                    }else{
                        // Failed to delete user record
                        showDialog(task.exception?.message!!)
                    }
                }
            }
        }
    }
}