package  com.example.bridgestore

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bridgestore.adapters.ChatAdapter
import com.example.bridgestore.model.Message
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ChatActivity : AppCompatActivity(), ChatAdapter.OnItemClickListener {

    private lateinit var db: FirebaseFirestore
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messages: MutableList<Message>

    private lateinit var messageField:EditText
    private  lateinit var sendButton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageField = findViewById(R.id.send_field)
        sendButton = findViewById(R.id.send_button)

        db = FirebaseFirestore.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        messages = mutableListOf()
        chatAdapter = ChatAdapter(messages, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        messageFetcher()
    }


    fun messageFetcher(){
        db.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Error loading messages: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                messages.clear()
                for (document in value!!) {
                    val message:Message = document.toObject(Message::class.java)
                    message.id = document.id
                    messages.add(message)
                }
                chatAdapter.notifyDataSetChanged()
            }

        sendButton.setOnClickListener(){
            sendMessage()
        }
    }

    override fun onItemClick(message: Message) {
        if (FirebaseAuth.getInstance().currentUser?.uid == message.senderId) {
            showEditDialog(message)
        }
    }

    fun sendMessage() {
       val text:String = messageField.text.toString()


        if (text.isNotBlank()) {
            val message = Message(
                null,
                text,
                FirebaseAuth.getInstance().currentUser!!.uid,
                Timestamp.now(),
            )
            db.collection("messages").add(message)
            messageField.text.clear()
        }
    }



    private fun showEditDialog(message: Message) {
        val builder :AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Edit message")

        val input = EditText(this)
        input.setText(message.text)
        builder.setView(input)
        val dialog = builder.create()
        builder.setPositiveButton("Update") { _, _ ->
            val updatedText = input.text.toString()
            updateMessage(message, updatedText)
            dialog.dismiss()
        }
        builder.setNegativeButton("Delete") { _, _ ->
            deleteMessage(message,)
            dialog.dismiss()
        }
        builder.setNeutralButton("Cancel", null)

        builder.show()
    }

    private fun updateMessage(message: Message, updatedText: String) {
        val m :Message = Message(message.id,updatedText,FirebaseAuth.getInstance().currentUser!!.uid,message.timestamp)
       FirebaseFirestore.getInstance().collection("messages").document(message.id!!).set(m).addOnCompleteListener() { vals->
           messages.clear()
           messageFetcher()

       };
    }

    private fun deleteMessage(message: Message) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to delete this message?")

        builder.setPositiveButton("YES") { dialog, which ->
            FirebaseFirestore.getInstance().collection("messages").document(message.id!!).delete().addOnCompleteListener() { vals->
                messages.clear()
                messageFetcher()
            };
        }

        builder.setNegativeButton("NO") { dialog, which ->
            // Do nothing
        }

        val dialog = builder.create()
        dialog.show()
    }

}
