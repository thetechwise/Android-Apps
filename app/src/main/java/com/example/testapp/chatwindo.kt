package com.example.testapp

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Date


import java.util.ArrayList;


class ChatWindowActivity : AppCompatActivity() {

    private lateinit var receiverImg: String
    private lateinit var receiverUid: String
    private lateinit var receiverName: String
    private lateinit var senderUid: String

    private lateinit var profile: CircleImageView
    private lateinit var receiverNameTextView: TextView
    private lateinit var sendBtn: CardView
    private lateinit var messageInput: EditText

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messagesAdapter: messagesAdpter
    private val messagesList = ArrayList<msgModelclass>()

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String

    companion object {
        lateinit var senderImg: String
        lateinit var receiverImageStatic: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatwindo)
        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get data from intent
        receiverName = intent.getStringExtra("nameeee").orEmpty()
        receiverImg = intent.getStringExtra("reciverImg").orEmpty()
        receiverUid = intent.getStringExtra("uid").orEmpty()

        // Initialize views
        sendBtn = findViewById(R.id.sendbtnn)
        messageInput = findViewById(R.id.textmsg)
        receiverNameTextView = findViewById(R.id.recivername)
        profile = findViewById(R.id.profileimgg)
        messageRecyclerView = findViewById(R.id.msgadpter)

        // Setup RecyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        messageRecyclerView.layoutManager = layoutManager

        messagesAdapter = messagesAdpter(this, messagesList)
        messageRecyclerView.adapter = messagesAdapter

        // Set profile and name
        Picasso.get().load(receiverImg).into(profile)
        receiverNameTextView.text = receiverName

        senderUid = auth.uid.orEmpty()
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        val userReference = database.reference.child("user").child(senderUid)
        val chatReference = database.reference.child("chats").child(senderRoom).child("messages")

        // Listen for message changes
        chatReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (dataSnapshot in snapshot.children) {
                    val message = dataSnapshot.getValue(msgModelclass::class.java)
                    message?.let { messagesList.add(it) }
                }
                messagesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Load sender image
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                senderImg = snapshot.child("profilepic").getValue(String::class.java).orEmpty()
                receiverImageStatic = receiverImg
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Send message
        sendBtn.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isEmpty()) {
                Toast.makeText(this, "Enter The Message First", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            messageInput.setText("")
            val message = msgModelclass(messageText, senderUid, Date().time)

            database.reference.child("chats").child(senderRoom)
                .child("messages").push().setValue(message).addOnCompleteListener {
                    database.reference.child("chats").child(receiverRoom)
                        .child("messages").push().setValue(message)
                }
        }
    }
}