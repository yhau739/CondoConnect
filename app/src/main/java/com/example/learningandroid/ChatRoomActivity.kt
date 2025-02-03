package com.example.learningandroid

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: MutableList<ChatMessage>
    private lateinit var chatRoomRef: DatabaseReference
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: ImageView
    private lateinit var usersRef: DatabaseReference
    private lateinit var clubRef: DatabaseReference
    private var clubId: String? = null
    private lateinit var chatRoomTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        // Get the club ID from the intent
        clubId = intent.getStringExtra("CLUB_ID")

        if (clubId == null) {
            finish() // Close the activity if no club ID is provided
            return
        }

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)
        chatRoomTitle = findViewById(R.id.chatRoomTitle)

        messageList = mutableListOf()
        chatAdapter = ChatAdapter(messageList)
        recyclerViewMessages.layoutManager = LinearLayoutManager(this)
        recyclerViewMessages.adapter = chatAdapter

        // Reference to the chat room specific to the club
        chatRoomRef = MainActivity.getDatabase().getReference("Clubs").child(clubId!!).child("chats")
        usersRef = MainActivity.getDatabase().getReference("Users")
        clubRef = MainActivity.getDatabase().getReference("Clubs").child(clubId!!)

        buttonSend.setOnClickListener { sendMessage() }
        findViewById<View>(R.id.backArrow).setOnClickListener { finish() }

        fetchClubName()
        fetchMessages()
    }

    private fun fetchClubName() {
        clubRef.child("title").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val clubName = snapshot.getValue(String::class.java) ?: "Club Chat"
                chatRoomTitle.text = clubName
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun fetchMessages() {
        chatRoomRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    messageList.add(it)
                    chatAdapter.notifyItemInserted(messageList.size - 1)
                    recyclerViewMessages.scrollToPosition(messageList.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val message = editTextMessage.text.toString()
        if (message.trim().isNotEmpty()) {
            val senderId = FirebaseAuth.getInstance().currentUser!!.uid

            // Fetch the sender's name from the "Users" table
            usersRef.child(senderId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val senderName = snapshot.child("username").getValue(String::class.java) ?: "Anonymous"
                    val messageId = chatRoomRef.push().key ?: return

                    val chatMessage = ChatMessage(
                        id = messageId,
                        senderId = senderId,
                        senderName = senderName,
                        content = message)
                    chatRoomRef.child(messageId).setValue(chatMessage).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            editTextMessage.text.clear()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }else {
            // add toast here
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}
