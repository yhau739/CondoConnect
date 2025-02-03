package com.example.learningandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learningandroid.models.ClubModel

class AdminClubAdapter(private val clubList: List<ClubModel>) :
    RecyclerView.Adapter<AdminClubAdapter.ClubViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_club, parent, false)
        return ClubViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubList[position]
        holder.clubTitle.text = club.title
        holder.clubDescription.text = club.description
        Glide.with(holder.itemView.context).load(club.imageUrl).into(holder.clubImage)

        holder.btnDeleteClub.setOnClickListener {
            deleteClub(club.id, holder.itemView.context)
        }
    }

    override fun getItemCount(): Int {
        return clubList.size
    }

    class ClubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clubImage: ImageView = itemView.findViewById(R.id.ivClubImage)
        val clubTitle: TextView = itemView.findViewById(R.id.tvClubTitle)
        val clubDescription: TextView = itemView.findViewById(R.id.tvClubDescription)
        val btnDeleteClub: Button = itemView.findViewById(R.id.btnDeleteClub)
    }

    private fun deleteClub(clubId: String, context: Context) {
        val database = MainActivity.getDatabase().getReference("Clubs").child(clubId)
        database.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Club deleted successfully", Toast.LENGTH_SHORT).show()
                // Redirect to Facility page
                val intent = Intent(context, AdminManageClub::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                if (context is Activity) {
                    context.finish()
                }
            } else {
                Toast.makeText(context, "Failed to delete club", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
