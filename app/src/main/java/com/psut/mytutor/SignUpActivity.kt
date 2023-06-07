package com.psut.mytutor

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class SignUpActivity : AppCompatActivity(), OnClickListener {

    private var emailAddressEt: EditText? = null
    private var passwordEt: EditText? = null
    private var nameEt: EditText? = null
    private var phoneNumberEt: EditText? = null
    private var majorEt: EditText? = null
    private var signUpBtn: Button? = null
    private var goToLoginPageTvBtn: TextView? = null
    private var progressBar: ProgressBar? = null

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth =
            FirebaseAuth.getInstance() // Here we are initializing the Firebase Authentication object

        nameEt = findViewById(R.id.etName)
        phoneNumberEt = findViewById(R.id.etPhoneNumber)
        majorEt = findViewById(R.id.etMajor)
        emailAddressEt = findViewById(R.id.etEmail)
        passwordEt = findViewById(R.id.etPassword)
        signUpBtn = findViewById(R.id.btnSignUp)
        goToLoginPageTvBtn = findViewById(R.id.tvGoToLogInPageBtn)
        progressBar = findViewById(R.id.pbLoader)

        signUpBtn?.setOnClickListener(this)
        goToLoginPageTvBtn?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btnSignUp) {
            ///TODO Sign up user
            signUpUser()
        } else if (v?.id == R.id.tvGoToLogInPageBtn) {
            ///TODO Navigate to login page
            navigateToLoginPage()
        }
    }

    private fun navigateToLoginPage() {
        startActivity(Intent(this, MainActivity::class.java))
        //Pop up stack
    }

    private fun signUpUser() {
        progressBar?.visibility = View.VISIBLE

        val name = nameEt?.text.toString()
        val phoneNumber = phoneNumberEt?.text.toString()
        val major = majorEt?.text.toString()
        val email = emailAddressEt?.text.toString()
        val password = passwordEt?.text.toString()

        if (name.isEmpty()) {
            showDialog("Please enter your name")
            return
        }
        if (phoneNumber.isEmpty()) {
            showDialog("Please enter your phone number")
            return
        }
        if (major.isEmpty()) {
            showDialog("Please enter your major")
            return
        }
        if (email.isEmpty()) {
            showDialog("Please enter your email address")
            return
        }
        if (password.isEmpty()) {
            showDialog("Please enter your password")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // The user is successfully registered
                    saveUserDataToDatabase()
                } else {
                    // The user is not successfully registered
                    showDialog(task.exception?.message.toString() /*Built in messages from Firebase*/)
                    progressBar?.visibility = View.GONE
                }
            }
    }

    private fun saveUserDataToDatabase() {
        val currentUser = auth.currentUser
        val name = nameEt?.text.toString()
        val phoneNumber = phoneNumberEt?.text.toString()
        val major = majorEt?.text.toString()
        val email = emailAddressEt?.text.toString()
        val password = passwordEt?.text.toString()

        val userData = UserData(
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            major = major,
            password = password,
            isAdmin = false,
            userUID = currentUser?.uid!!,
            rates = mutableListOf("0"),
            topics = mutableListOf(Topic())
        )
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUser.uid)
        databaseRef.setValue(userData)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    progressBar?.visibility = View.GONE
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                }else{
                    showDialog(task.exception?.message.toString() /*Built in message from Firebase*/)
                    progressBar?.visibility = View.GONE
                }
            }
    }

//    private fun getUserTopics(): MutableList<Topic> {
//        val topics = mutableListOf<Topic>()
//        val topic = Topic(
//            "Mobile Computing",
//            getTopicUniqeID()
//        )
//        val topic2 = Topic(
//            "Math",
//            getTopicUniqeID()
//        )
//        topics.add(topic)
//        topics.add(topic2)
//
//        return topics
//    }
//
//    private fun getTopicUniqeID(): String {
//        val uuid = UUID.randomUUID()
//        return uuid.toString()
//    }

    private fun showDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Invalid Input")
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK") { dialog, which ->
            // Do something when the "OK" button is clicked
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()

        progressBar?.visibility = View.GONE
    }
}