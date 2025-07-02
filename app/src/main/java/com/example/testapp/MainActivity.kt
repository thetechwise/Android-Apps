package com.example.testapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mainUserRecyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var usersArrayList: ArrayList<Users>
    private lateinit var imgLogout: ImageView
    private lateinit var camButton: ImageView
    private lateinit var settingsButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        camButton = findViewById(R.id.camBut)
        settingsButton = findViewById(R.id.settingBut)
        imgLogout = findViewById(R.id.logoutimg)
        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView)

        usersArrayList = ArrayList()

        mainUserRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(this, usersArrayList)
        mainUserRecyclerView.adapter = adapter

        val reference = database.reference.child("user")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersArrayList.clear()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(Users::class.java)
                    user?.let { usersArrayList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        imgLogout.setOnClickListener {
            val dialog = Dialog(this, R.style.dialoge)
            dialog.setContentView(R.layout.dialog_layout)
            val yesBtn = dialog.findViewById<Button>(R.id.yesbnt)
            val noBtn = dialog.findViewById<Button>(R.id.nobnt)

            yesBtn.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            noBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        camButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 10)
        }

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}