package com.example.testapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var logsignup: TextView
    private lateinit var button: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this).apply {
            setMessage("Please Wait...")
            setCancelable(false)
        }

        button = findViewById(R.id.logbutton)
        email = findViewById(R.id.editTexLogEmail)
        password = findViewById(R.id.editTextLogPassword)
        logsignup = findViewById(R.id.logsignup)

        logsignup.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }

        button.setOnClickListener {
            val Email = email.text.toString().trim()
            val pass = password.text.toString().trim()

            when {
                TextUtils.isEmpty(Email) -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Enter The Email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(pass) -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Enter The Password", Toast.LENGTH_SHORT).show()
                }
                !emailPattern.matches(Email) -> {
                    progressDialog.dismiss()
                    email.error = "Give Proper Email Address"
                }
                pass.length < 6 -> {
                    progressDialog.dismiss()
                    password.error = "More Than Six Characters"
                    Toast.makeText(this, "Password Needs To Be Longer Than Six Characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    progressDialog.show()
                    auth.signInWithEmailAndPassword(Email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(this, task.exception?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}