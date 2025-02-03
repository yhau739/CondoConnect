package com.example.learningandroid

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learningandroid.models.FeedbackModel

class FeedbackAdapter(private val feedbackList: List<FeedbackModel>) :
    RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feedback, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.feedbackTitle.text = feedback.title
        holder.feedbackType.text = feedback.type
        holder.feedbackContent.text = feedback.content
        holder.feedbackStatus.text = feedback.status
        if (feedback.status == "resolved") {
            holder.feedbackStatus.setTextColor(Color.GREEN)
        } else {
            holder.feedbackStatus.setTextColor(Color.RED)
        }

        // Assuming you have a URL for the feedback image, you can use Glide to load it
        Glide.with(holder.itemView.context).load(feedback.imageUrl).into(holder.feedbackImage)

        // Set onClickListener to navigate to FeedbackDetail activity
        holder.feedbackCardLayout.setOnClickListener {
            val intent = Intent(holder.itemView.context, FeedbackDetailActivity::class.java)
            intent.putExtra("FEEDBACK_ID", feedback.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }

    class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedbackImage: ImageView = itemView.findViewById(R.id.feedbackImage)
        val feedbackTitle: TextView = itemView.findViewById(R.id.feedbackTitle)
        val feedbackType: TextView = itemView.findViewById(R.id.feedbackType)
        val feedbackContent: TextView = itemView.findViewById(R.id.feedbackContent)
        val feedbackStatus: TextView = itemView.findViewById(R.id.feedbackStatus)
        val feedbackCardLayout: LinearLayout = itemView.findViewById(R.id.feedbackCardLayout)
    }
}
