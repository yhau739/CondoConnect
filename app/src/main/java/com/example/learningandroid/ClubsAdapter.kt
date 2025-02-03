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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction

class ClubsAdapter(private val clubList: List<ClubModel>) :
    RecyclerView.Adapter<ClubsAdapter.ClubViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_join_club, parent, false)
        return ClubViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubList[position]
        holder.clubTitle.text = club.title
        holder.clubDescription.text = club.description
        holder.clubMembers.text = "${club.memberCount} Members"
        Glide.with(holder.itemView.context).load(club.imageUrl).into(holder.clubImage)

        holder.btnJoinClub.setOnClickListener {
            joinClub(club.id, holder.itemView.context)
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
        val btnJoinClub: Button = itemView.findViewById(R.id.btnJoinClub)
    }

    private fun joinClub(clubId: String, context: Context) {
        val database = MainActivity.getDatabase().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val clubRef = database.child("Clubs").child(clubId)
        val membersRef = clubRef.child("members")
        val memberCountRef = clubRef.child("memberCount")
        val userRef = database.child("Users").child(userId).child("clubs")

        // Add user to club's members and increment member count
        membersRef.child(userId).setValue(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Increment the member count
                memberCountRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        var count = mutableData.getValue(Int::class.java) ?: 0
                        mutableData.value = count + 1
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        if (committed) {
                            // Add club to user's joined clubs
                            userRef.child(clubId).setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Joined club successfully", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(context, JoinClubsActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intent)
                                    if (context is Activity) {
                                        context.finish()
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to add club to user", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Failed to join club", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                Toast.makeText(context, "Failed to join club", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

//// Redirect to Facility page
//val intent = Intent(this, AdminManageClub::class.java)
//intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//startActivity(intent)
//finish()