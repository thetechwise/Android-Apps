package com.example.testapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView


class RegistrationActivity : AppCompatActivity() {

    private lateinit var loginbut: TextView
    private lateinit var rg_username: EditText
    private lateinit var rg_email: EditText
    private lateinit var rg_password: EditText
    private lateinit var rg_repassword: EditText
    private lateinit var rg_signup: Button
    private lateinit var rg_profileImg: CircleImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var imageURI: Uri? = null
    private lateinit var imageuri: String

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

        progressDialog = ProgressDialog(this).apply {
            setMessage("Establishing The Account")
            setCancelable(false)
        }

        // Firebase init
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        // UI init
        loginbut = findViewById(R.id.loginbut)
        rg_username = findViewById(R.id.rgusername)
        rg_email = findViewById(R.id.rgemail)
        rg_password = findViewById(R.id.rgpassword)
        rg_repassword = findViewById(R.id.rgrepassword)
        rg_profileImg = findViewById(R.id.profilerg0)
        rg_signup = findViewById(R.id.signupbutton)

        // Login button click
        loginbut.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Signup button click
        rg_signup.setOnClickListener {
            val name = rg_username.text.toString()
            val email = rg_email.text.toString()
            val password = rg_password.text.toString()
            val confirmPassword = rg_repassword.text.toString()
            val status = "Hey I'm Using This Application"

            when {
                name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show()
                }
                !emailPattern.matches(email) -> {
                    progressDialog.dismiss()
                    rg_email.error = "Type A Valid Email Here"
                }
                password.length < 6 -> {
                    progressDialog.dismiss()
                    rg_password.error = "Password Must Be 6 Characters Or More"
                }
                password != confirmPassword -> {
                    progressDialog.dismiss()
                    rg_password.error = "The Password Doesn't Match"
                }
                else -> {
                    progressDialog.show()
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val id = task.result.user?.uid ?: return@addOnCompleteListener
                            val reference = database.reference.child("user").child(id)
                            val storageReference = storage.reference.child("Upload").child(id)

                            if (imageURI != null) {
                                storageReference.putFile(imageURI!!).addOnCompleteListener { uploadTask ->
                                    if (uploadTask.isSuccessful) {
                                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                                            imageuri = uri.toString()
                                            val user = Users(id, name, email, password, imageuri, status)
                                            saveUser(reference, user)
                                        }
                                    }
                                }
                            } else {
                                imageuri = "https://firebasestorage.googleapis.com/v0/b/testapp-cf8a7.firebasestorage.app/o/man.png?alt=media&token=8d3f43cf-b923-4306-83b2-b80829ea2952"
                                val user = Users(id, name, email, password, imageuri, status)
                                saveUser(reference, user)
                            }
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this, task.exception?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Profile image click
        rg_profileImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"))
        }
    }

    private fun saveUser(reference: DatabaseReference, user: Users) {
        reference.setValue(user).addOnCompleteListener { task ->
            progressDialog.dismiss()
            if (task.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error in creating the user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                imageURI = result.data!!.data
                rg_profileImg.setImageURI(imageURI)
            }
        }
}