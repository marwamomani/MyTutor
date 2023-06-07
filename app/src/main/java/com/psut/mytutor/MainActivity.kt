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
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), OnClickListener {

    private var emailAddressEt: EditText? = null
    private var passwordEt: EditText? = null
    private var loginBtn: Button? = null
    private var goToSignUpPageTvBtn: TextView? = null
    private var progressBar: ProgressBar? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        emailAddressEt = findViewById(R.id.etEmail)
        passwordEt = findViewById(R.id.etPassword)
        loginBtn = findViewById(R.id.btnLogin)
        goToSignUpPageTvBtn = findViewById(R.id.tvGoToSignUpPageBtn)
        progressBar = findViewById(R.id.pbLoader)

        loginBtn?.setOnClickListener(this)
        goToSignUpPageTvBtn?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.btnLogin){
            ///TODO Login user
            loginUser()
        }else if(v?.id == R.id.tvGoToSignUpPageBtn){
            ///TODO Navigates the user to sign up page
            navigateToSignUpPage()
        }
    }

    private fun navigateToSignUpPage(){
        startActivity(Intent(this, SignUpActivity::class.java))
        //Pop up stack
    }
    private fun loginUser() {
        progressBar?.visibility = View.VISIBLE
        val email = emailAddressEt?.text.toString()
        val password = passwordEt?.text.toString()

        if(email.isEmpty()){
            showDialog("Please enter your email address")
            return
        }
        if(password.isEmpty()){
            showDialog("Please enter your password")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    /// The user is successfully signed in
                    // Now we check if the user is blocked
                    val userID = auth.currentUser?.uid
                    val databaseRef = FirebaseDatabase.getInstance().reference.child("blocked_users").child(userID!!)
                    databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                // The user is blocked
                                showDialog("This user deleted by admin and can not log in")
                            }else{
                                // The user is not blocked
                                if(email =="admin@mytutor.com"){

                                    startActivity(Intent(this@MainActivity, AdminActivity::class.java))
                                }else{
                                    startActivity(Intent(this@MainActivity, UserHomePageActivity::class.java))
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })

                    progressBar?.visibility = View.GONE
                }else{
                    /// The user is not successfully signed in
                    showDialog(task.exception?.message.toString() /*Built in messages from Firebase*/)
                    progressBar?.visibility = View.GONE
                }
            }
    }

    private fun showDialog(message: String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Invalid Input")
        dialogBuilder.setMessage(message)
        dialogBuilder.setPositiveButton("OK"){dialog, which ->
            // Do something when the "OK" button is clicked
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()

        progressBar?.visibility = View.GONE
    }
}