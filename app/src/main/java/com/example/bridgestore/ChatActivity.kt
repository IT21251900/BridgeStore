package com.example.bridgestore
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bridgestore.adapters.MessageAdapter
import com.example.bridgestore.model.ChatMessage
import com.example.bridgestore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ChatActivity : AppCompatActivity() {
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter

    private lateinit var firestore: FirebaseFirestore
    private lateinit var messageCollection: CollectionReference

    private lateinit var auth: FirebaseAuth

    private lateinit var chatId: String

    private lateinit var reciverId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        /*firestore = FirebaseFirestore.getInstance()

        messageCollection = firestore.collection("messages")

        reciverId  = intent.getStringExtra("sellerId") ?: ""
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        messageRecyclerView = findViewById(R.id.messageRecyclerView)

        messageAdapter = MessageAdapter()
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        chatId = intent.getStringExtra("chatId") ?: intent.getStringExtra("sellerId") + auth.currentUser!!.uid

        if (chatId.isNotEmpty()) {
            messageCollection = firestore.collection("chats").document(chatId).collection("messages")



            retrieveMessages()
        } else {
            Log.e(TAG, "No chat ID provided.")
        }
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }*/
    }

    private fun sendMessage(messageText: String) {
        val currentUser = auth.currentUser
        val message = hashMapOf(
            "senderId" to (currentUser?.uid ?: ""),
            "messageText" to messageText,
            "timestamp" to FieldValue.serverTimestamp()
        )
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        messageCollection.document(uid+reciverId).set(message)
            .addOnSuccessListener {
                Log.d(TAG, "Message sent successfully.")
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to send message: ${it.message}")
            }
    }

    private fun retrieveMessages() {
        messageCollection.orderBy("timestamp")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e(TAG, "Failed to retrieve messages: ${error.message}")
                        return
                    }

                    snapshot?.let { querySnapshot ->
                        val messages = mutableListOf<ChatMessage>()
                        for (document in querySnapshot.documents) {
                            val message = document.toObject(ChatMessage::class.java)
                            message?.let { messages.add(it) }
                        }
                        messageAdapter.setMessages(messages)
                        messageRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                    }
                }
            })
    }

    companion object {
        private const val TAG = "ChatActivity"
    }
}
