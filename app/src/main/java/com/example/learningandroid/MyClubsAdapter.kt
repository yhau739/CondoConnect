package com.example.learningandroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learningandroid.models.ClubModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Transaction
import com.google.firebase.database.MutableData
import com.google.firebase.database.DatabaseError

class MyClubsAdapter(private val clubList: List<ClubModel>) :
    RecyclerView.Adapter<MyClubsAdapter.ClubViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_club, parent, false)
        return ClubViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubList[position]
        holder.clubTitle.text = club.title
        holder.clubDescription.text = club.description
        holder.clubMembers.text = "${club.memberCount} Members"
        Glide.with(holder.itemView.context).load(club.imageUrl).into(holder.clubImage)

        holder.btnLeaveClub.setOnClickListener {
            leaveClub(club.id, holder.itemView.context)
        }

        holder.clubCardLayout.setOnClickListener {
            val intent = Intent(holder.itemView.context, ChatRoomActivity::class.java)
            intent.putExtra("CLUB_ID", club.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return clubList.size
    }

    class ClubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clubImage: ImageView = itemView.findViewById(R.id.ivClubImage)
        val clubTitle: TextView = itemView.findViewById(R.id.tvClubTitle)
        val clubDescription: TextView = itemView.findViewById(R.id.tvClubDescription)
        val clubMembers: TextView = itemView.findViewById(R.id.tvClubMembers)
        val btnLeaveClub: Button = itemView.findViewById(R.id.btnLeaveClub)
        val clubCardLayout: LinearLayout = itemView.findViewById(R.id.clubCardLayout)
    }

    private fun leaveClub(clubId: String, context: Context) {
        val database = MainActivity.getDatabase().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val clubRef = database.child("Clubs").child(clubId)
        val membersRef = clubRef.child("members")
        val memberCountRef = clubRef.child("memberCount")
        val userRef = database.child("Users").child(userId).child("clubs")

        // Remove user from club's members and decrement member count
        membersRef.child(userId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Decrement the member count
                memberCountRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        var count = mutableData.getValue(Int::class.java) ?: return Transaction.success(mutableData)
                        if (count > 0) {
                            mutableData.value = count - 1
                        }
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        if (committed) {
                            // Remove club from user's joined clubs
                            userRef.child(clubId).removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Left club successfully", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, MyClubsActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intent)
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to remove club from user", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Failed to leave club", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                Toast.makeText(context, "Failed to leave club", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
