package com.psut.mytutor

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import java.util.*

class UserProfileActivity : AppCompatActivity(), View.OnClickListener {

    private var toolbar: Toolbar? = null
    private var nameTv: TextView? = null
    private var majorTv: TextView? = null
    private var phoneNumberTv: TextView? = null
    private var rateTv: TextView? = null
    private var topicsRv: RecyclerView? = null
    private var addMoreTopicsBtn: Button? = null
    private var loaderPb: ProgressBar? = null
    private lateinit var adapter: UserProfileTopicsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        toolbar = findViewById(R.id.toolbar)
        nameTv = findViewById(R.id.tvUserNameValue)
        majorTv = findViewById(R.id.tvMajorValue)
        phoneNumberTv = findViewById(R.id.tvPhoneNumberValue)
        rateTv = findViewById(R.id.tvUserRateValue)
        topicsRv = findViewById(R.id.rvUserTopics)
        addMoreTopicsBtn = findViewById(R.id.btnAddMoreTopics)
        loaderPb = findViewById(R.id.pbLoader)

        addMoreTopicsBtn?.setOnClickListener(this)

        fetchUserData()
    }

    private fun fetchUserData() {
        loaderPb?.visibility = View.VISIBLE
        val currentUser = FirebaseAuth.getInstance().currentUser
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUser?.uid!!)
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    // The user data is fetched successfully
                    val userData = snapshot.getValue(UserData::class.java)
                    showUserData(userData)
                }else{
                    // The user data is not found in the DB
                    showDialog("The user does not have any records")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // An error occurred while retrieving the data
                showDialog(error.message /*Built in error message from Firebase*/)
            }
        })
    }

    private fun showUserData(userData: UserData?) {
        toolbar?.title = "Welcome "+userData?.name
        nameTv?.text = userData?.name
        majorTv?.text = userData?.major
        phoneNumberTv?.text = userData?.phoneNumber
        rateTv?.text = calculateRate(userData?.rates)
        showUserTopics(userData)
    }

    private fun calculateRate(rates: MutableList<String>?): String? {
        if(rates?.isEmpty() == true){
            return "0"
        }else{
            val ratesCount = rates?.count()
            val intRates = rates?.map { it.toInt() }
            val average = intRates?.average()
            return average.toString()
        }
    }

    private fun showUserTopics(userData: UserData?) {
        val topics = userData?.topics
        if(topics?.isEmpty() == false){
            val filteredTopics = topics.filter { it.id != ""}
            adapter = UserProfileTopicsAdapter(filteredTopics)
            topicsRv?.setHasFixedSize(true)
            topicsRv?.layoutManager = LinearLayoutManager(this)
            topicsRv?.adapter = adapter
        }
        loaderPb?.visibility = View.GONE
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
        loaderPb?.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.btnAddMoreTopics){
            /// Show a pop-up to add more topics
            showAddMoreTopicsDialog()
        }
    }

    private fun showAddMoreTopicsDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Add Topic")
        dialogBuilder.setMessage("Type in your new topic")

        val inputEditText = EditText(this)
        dialogBuilder.setView(inputEditText)

        dialogBuilder.setPositiveButton("Add"){dialog, which ->
            dialog.dismiss()
            val userInput = inputEditText.text.toString()
            if(userInput.isEmpty()){
                inputEditText.error = "Please type your topic"
            }else{
                sendAddedTopicToDatabase(userInput)
            }
        }
        dialogBuilder.setNegativeButton("Cancel"){dialog, which ->
            dialog.dismiss()
            dialog.cancel()
        }
        dialogBuilder.show()
    }


    /*
    * 1- Retrieve user's existing topics from the database
    * 2- Convert the data snapshot coming from the database to MutableList, so we can add new topics or modify it
    * 3- Add the new topic that the user entered to the list
    * 4- Resend the updated list of topics to the database again
    * */
    private fun sendAddedTopicToDatabase(userInput: String) {
        loaderPb?.visibility = View.VISIBLE
        val topic = Topic(userInput, generateUniqueID())

        val currentUser = FirebaseAuth.getInstance().currentUser
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUser?.uid.toString()).child("topics")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    // The user's topics are fetched successfully
                    val currentTopics = snapshot.getValue(object : GenericTypeIndicator<MutableList<Topic>>() {})
                    currentTopics?.add(topic)
                    databaseRef.setValue(currentTopics)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                // The topic is added successfully to the database
                                loaderPb?.visibility = View.GONE
                                fetchUserData()
                            }else{
                                // The topic is not added successfully to the database
                                showDialog(task.exception?.message.toString())
                            }
                        }
                }else{
                    // The user's topics are not found in the DB
                    showDialog("The user does not have any topics")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showDialog(error.message /*Built in error message from Firebase*/)
            }
        })
    }

    private fun generateUniqueID(): String{
        val topicID = UUID.randomUUID().toString()
        return topicID
    }
}