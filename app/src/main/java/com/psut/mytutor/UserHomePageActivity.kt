package com.psut.mytutor

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserHomePageActivity : AppCompatActivity(), TutorNameClicked, OnClickListener {

    private var toolbar: Toolbar? = null
    private var rvAnnouncements: RecyclerView? = null
    private var pbLoader: ProgressBar? = null
    private var openProfilePageTv: TextView? = null
    private var logoutTv: TextView? = null

    private lateinit var adapter: UserHomePageAnnouncementsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home_page)

        toolbar = findViewById(R.id.toolbar)
        rvAnnouncements = findViewById(R.id.rvAnnouncements)
        pbLoader = findViewById(R.id.pbLoader)
        openProfilePageTv = findViewById(R.id.tvOpenUserProfilePage)
        logoutTv = findViewById(R.id.tvLogout)
        openProfilePageTv?.setOnClickListener(this)
        logoutTv?.setOnClickListener(this)
        fetchAnnouncementsList()
    }

    private fun fetchAnnouncementsList() {
        pbLoader?.visibility = View.VISIBLE
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val users = mutableListOf<UserData>()
                    snapshot.children.forEach { user->
                        val userData: UserData = user.getValue(UserData::class.java)!!
                        users.add(userData)
                    }
                    extractAnnouncementsFromUsers(users)
                }else{
                    showDialog("There is no announcements found!")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                showDialog(error.message /*Built in message from Firebase*/)
            }
        })
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
        pbLoader?.visibility = View.GONE
    }
    private fun extractAnnouncementsFromUsers(users: MutableList<UserData>?) {
        val announcements = mutableListOf<Announcement>()
        users?.forEach { user->
            user.topics.forEach { topic ->
                if(topic.id == "")
                    return@forEach
                val announcement =  Announcement(topic = topic, userName = user.name, userID = user.userUID)
                announcements.add(announcement)
            }
        }
        adapter = UserHomePageAnnouncementsAdapter(announcements, this)
        rvAnnouncements?.setHasFixedSize(true)
        rvAnnouncements?.layoutManager = LinearLayoutManager(this)
        rvAnnouncements?.adapter = adapter
        pbLoader?.visibility = View.GONE
    }

    override fun openProfilePage(userUID: String) {
        val intent = Intent(this, TutorProfileActivity::class.java)
        intent.putExtra("id", userUID) // Here we are adding the user unique ID as an extra with the Intent.
        startActivity(intent)
    }
    private fun openUserProfilePage() {
        startActivity(Intent(this@UserHomePageActivity, UserProfileActivity::class.java))
    }
    override fun onClick(v: View?) {
        if(v?.id == R.id.tvOpenUserProfilePage){
            openUserProfilePage()
        }else if(v?.id == R.id.tvLogout){
            logoutUser()
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this@UserHomePageActivity, MainActivity::class.java))
    }
}