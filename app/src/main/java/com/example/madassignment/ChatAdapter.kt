package com.example.madassignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val chatMessages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.bind(chatMessage)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewUserName: TextView
        private val textViewMessage: TextView
        private val textViewTimestamp: TextView

        init {
            textViewUserName = itemView.findViewById<TextView>(R.id.textViewUserName)
            textViewMessage = itemView.findViewById<TextView>(R.id.textViewMessage)
            textViewTimestamp = itemView.findViewById<TextView>(R.id.textViewTimestamp)
        }

        fun bind(chatMessage: ChatMessage) {
            // Example: Display user name, message, and timestamp
            // You may need to fetch user names from Firestore based on userId
            // For simplicity, I'm showing only the message and timestamp here
            textViewUserName.text = chatMessage.displayName
            textViewMessage.text = chatMessage.message
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(chatMessage.timestamp))
            textViewTimestamp.text = formattedDate
        }
    }
}