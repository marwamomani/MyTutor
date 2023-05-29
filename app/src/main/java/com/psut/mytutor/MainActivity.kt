package com.psut.mytutor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), OnClickListener {

    private var emailAddressEt: EditText? = null
    private var passwordEt: EditText? = null
    private var loginBtn: Button? = null
    private var goToSignUpPageTvBtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailAddressEt = findViewById(R.id.etEmail)
        passwordEt = findViewById(R.id.etPassword)
        loginBtn = findViewById(R.id.btnLogin)
        goToSignUpPageTvBtn = findViewById(R.id.tvGoToSignUpPageBtn)

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
    }
}