package com.psut.mytutor

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class TutorProfileActivity : AppCompatActivity(), View.OnClickListener {

    private var toolbar: Toolbar? = null
    private var nameTv: TextView? = null
    private var majorTv: TextView? = null
    private var phoneNumberTv: TextView? = null
    private var rateTv: TextView? = null
    private var topicsRv: RecyclerView? = null
    private var rateButton: Button? = null
    private var loaderPb: ProgressBar? = null
    private lateinit var adapter: UserProfileTopicsAdapter
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_profile)

        userID = intent.getStringExtra("id")!! // Here we are fetching the user ID coming from the HomePageActivity

        toolbar = findViewById(R.id.toolbar)
        nameTv = findViewById(R.id.tvUserNameValue)
        majorTv = findViewById(R.id.tvMajorValue)
        phoneNumberTv = findViewById(R.id.tvPhoneNumberValue)
        rateTv = findViewById(R.id.tvUserRateValue)
        topicsRv = findViewById(R.id.rvUserTopics)
        rateButton = findViewById(R.id.btnRate)
        loaderPb = findViewById(R.id.pbLoader)

        rateButton?.setOnClickListener(this)

        fetchUserData()
    }

    private fun fetchUserData() {
        loaderPb?.visibility = View.VISIBLE
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userID)
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    // The user data is fetched successfully
                    val userData = snapshot.getValue(UserData::class.java)
                    showUserData(userData)
                }else{
                    // The user data is not found
                    showDialog("The user does not have any records")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Something went wrong
                showDialog(error.message /*Built in error message from Firebase*/)
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
        loaderPb?.visibility = View.GONE
    }
    private fun showUserData(userData: UserData?) {
        toolbar?.title = userData?.name
        nameTv?.text = userData?.name
        majorTv?.text = userData?.major
        phoneNumberTv?.text = userData?.phoneNumber
        rateTv?.text = calculateRate(userData?.rates)
        showUserTopics(userData)
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

    private fun calculateRate(rates: MutableList<String>?): String? {
        if(rates?.isEmpty() == true){
            return "0"
        }else{
            val intRates = rates?.filter { it != "0" }?.map { it.toInt() }
            val average = intRates?.average()
            saveNewRateAverageToDatabase(average)
            return average.toString()
        }
    }

    private fun saveNewRateAverageToDatabase(average: Double?) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userID).child("rateAverage")
        databaseRef.setValue(average.toString()).addOnCompleteListener { task->
            if(!task.isSuccessful){
                showDialog(task.exception?.message.toString())
            }
        }
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.btnRate){
            showRateDialog()
        }
    }

    private fun showRateDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Rate tutor")
        dialogBuilder.setMessage("Rate this tutor from 1 to 5")

        val spinner = Spinner(this)
        val options = arrayOf(1,2,3,4,5)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.adapter = adapter
        dialogBuilder.setView(spinner)

        dialogBuilder.setPositiveButton("Rate"){dialog, which ->
            val userInput = spinner.selectedItem.toString().toInt()
            sendNewRateToDatabase(userInput)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel"){dialog, which->
            dialog.dismiss()
            dialog.cancel()
        }
        dialogBuilder.show()
    }

    private fun sendNewRateToDatabase(userInput: Int) {
        loaderPb?.visibility = View.VISIBLE
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userID).child("rates")
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    // The user's old rates are fetched
                    val rates = snapshot.getValue(object : GenericTypeIndicator<MutableList<String>>(){})
                    rates?.add(userInput.toString())
                    databaseRef.setValue(rates).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            // The new rate has been added successfully
                            loaderPb?.visibility = View.GONE

                            fetchUserData()
                        }else{
                            showDialog(task.exception?.message.toString())
                        }
                    }
                }else{
                    // The user's rates are not found in the DB
                    showDialog("The user does not have any records")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showDialog(error.message)
            }
        })
    }
}