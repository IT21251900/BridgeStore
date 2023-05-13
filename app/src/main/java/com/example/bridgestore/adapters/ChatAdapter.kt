package com.example.bridgestore.adapters
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bridgestore.R
import com.example.bridgestore.model.Message
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val messages: List<Message>,private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (firebaseAuth.currentUser?.uid == message.senderId) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            LayoutInflater.from(parent.context).inflate(R.layout.message_sent, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.message_recived, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val textView: TextView = itemView.findViewById(R.id.text_view)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(message: Message) {
            textView.text = message.text
        }

        override fun onClick(view: View?) {
            val message = messages[adapterPosition]
            itemClickListener.onItemClick(message)
        }

    }
    interface OnItemClickListener {
        fun onItemClick(message: Message)
    }
}

