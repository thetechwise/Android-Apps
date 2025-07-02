package com.example.testapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class SettingActivity : AppCompatActivity() {

    private lateinit var setProfile: ImageView
    private lateinit var setName: EditText
    private lateinit var setStatus: EditText
    private lateinit var doneButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog

    private var setImageUri: Uri? = null
    private lateinit var email: String
    private lateinit var password: String

    private lateinit var reference: DatabaseReference
    private lateinit var storageReference: com.google.firebase.storage.StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar?.hide()

        // Firebase setup
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        // Views
        setProfile = findViewById(R.id.settingprofile)
        setName = findViewById(R.id.settingname)
        setStatus = findViewById(R.id.settingstatus)
        doneButton = findViewById(R.id.donebutt)

        // Progress dialog
        progressDialog = ProgressDialog(this).apply {
            setMessage("Saving...")
            setCancelable(false)
        }

        reference = database.getReference("user").child(auth.uid!!)
        storageReference = storage.reference.child("upload").child(auth.uid!!)

        // Load existing data
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                email = snapshot.child("mail").value.toString()
                password = snapshot.child("password").value.toString()
                val name = snapshot.child("userName").value.toString()
                val profile = snapshot.child("profilepic").value.toString()
                val status = snapshot.child("status").value.toString()

                setName.setText(name)
                setStatus.setText(status)
                Picasso.get().load(profile).into(setProfile)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Pick image
        setProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            imagePicker.launch(Intent.createChooser(intent, "Select Picture"))
        }

        // Save updated data
        doneButton.setOnClickListener {
            progressDialog.show()

            val name = setName.text.toString()
            val status = setStatus.text.toString()

            if (setImageUri != null) {
                storageReference.putFile(setImageUri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnSuccessListener { uri ->
                            saveUserData(uri.toString(), name, status)
                        }
                    }
                }
            } else {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    saveUserData(uri.toString(), name, status)
                }
            }
        }
    }

    private val imagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            setImageUri = result.data?.data
            setProfile.setImageURI(setImageUri)
        }
    }

    private fun saveUserData(imageUri: String, name: String, status: String) {
        val user = Users(auth.uid, name, email, password, imageUri, status)
        reference.setValue(user).addOnCompleteListener { task ->
            progressDialog.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(this, "Data is saved", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}