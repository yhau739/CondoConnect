package com.example.learningandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learningandroid.models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val messageList: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messageList[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (message.senderId == currentUserId) {
            holder.senderName.text = "You"
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primaryPurple))
            holder.senderName.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            holder.messageContent.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            holder.messageTime.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            holder.profileImage.setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
        } else {
            holder.senderName.text = message.senderName
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            holder.senderName.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            holder.messageContent.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            holder.messageTime.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
        }

        holder.messageContent.text = message.content
        holder.messageTime.text = formatTime(message.timestamp)

        // Placeholder image for profile, you can load actual user profile image if available
        Glide.with(holder.itemView.context)
            .load(R.drawable.baseline_people_24)
            .into(holder.profileImage)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.imageViewProfile)
        val senderName: TextView = itemView.findViewById(R.id.textViewSender)
        val messageContent: TextView = itemView.findViewById(R.id.textViewMessage)
        val messageTime: TextView = itemView.findViewById(R.id.textViewTime)
    }

    private fun formatTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val today = Calendar.getInstance()
        return if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            "Today " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
        } else {
            SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
