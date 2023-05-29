package com.psut.mytutor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class SignUpActivity : AppCompatActivity(), OnClickListener {

    private var emailAddressEt: EditText? = null
    private var passwordEt: EditText? = null
    private var signUpBtn: Button? = null
    private var goToLoginPageTvBtn: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        emailAddressEt = findViewById(R.id.etEmail)
        passwordEt = findViewById(R.id.etPassword)
        signUpBtn = findViewById(R.id.btnSignUp)
        goToLoginPageTvBtn = findViewById(R.id.tvGoToLogInPageBtn)

        signUpBtn?.setOnClickListener(this)
        goToLoginPageTvBtn?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.btnSignUp){
            ///TODO Sign up user
            signUpUser()
        }else if(v?.id == R.id.tvGoToLogInPageBtn){
            ///TODO Navigate to login page
            navigateToLoginPage()
        }
    }

    private fun navigateToLoginPage() {
        startActivity(Intent(this, MainActivity::class.java))
        //Pop up stack
    }

    private fun signUpUser() {
    }
}