package com.example.bridgestore.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bridgestore.R
import com.example.bridgestore.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages: MutableList<ChatMessage> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun setMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        fun bind(message: ChatMessage) {
            messageText.text = message.message

            val currentUser = FirebaseAuth.getInstance().currentUser
            val isCurrentUser = currentUser != null && currentUser.uid == message.senderId

            val layoutParams = messageText.layoutParams as ViewGroup.MarginLayoutParams
            if (isCurrentUser) {
                layoutParams.marginStart = 100
                layoutParams.marginEnd = 0
                messageText.setBackgroundResource(R.drawable.sender_message_background)
            } else {
                layoutParams.marginStart = 0
                layoutParams.marginEnd = 100
                messageText.setBackgroundResource(R.drawable.recipient_message_background)
            }
            messageText.layoutParams = layoutParams
        }
    }
}
