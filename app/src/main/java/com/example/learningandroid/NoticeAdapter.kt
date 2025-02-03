package com.example.learningandroid

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.NoticeModel
import com.bumptech.glide.Glide
import com.example.learningandroid.models.FetchNoticeModel

class NoticeAdapter(private val notices: List<FetchNoticeModel>, private val isAdmin: Boolean) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notice, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = notices[position]
        holder.bind(notice, isAdmin)
    }

    override fun getItemCount(): Int = notices.size

    inner class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val noticeImage: ImageView = itemView.findViewById(R.id.ivNoticeImage)
        private val noticeTitle: TextView = itemView.findViewById(R.id.tvNoticeTitle)
        private val noticeSubtitle: TextView = itemView.findViewById(R.id.tvNoticeSubtitle)
        private val noticeDate: TextView = itemView.findViewById(R.id.tvNoticeDate)
        private val noticeDescription: TextView = itemView.findViewById(R.id.tvNoticeDescription)
        private val btnDeleteNotice: Button = itemView.findViewById(R.id.btnDeleteNotice)

        fun bind(notice: FetchNoticeModel, isAdmin: Boolean) {
            Glide.with(itemView.context)
                .load(notice.imageUrl)
                .into(noticeImage)

            noticeTitle.text = notice.title
            noticeSubtitle.text = notice.subtitle
            noticeDate.text = notice.date
            noticeDescription.text = notice.description

            itemView.findViewById<LinearLayout>(R.id.noticeCardLayout).setOnClickListener {
                val intent = Intent(itemView.context, NoticeDetailActivity::class.java).apply {
                    putExtra("NOTICE_TITLE", notice.title)
                    putExtra("NOTICE_SUBTITLE", notice.subtitle)
                    putExtra("NOTICE_DATE", notice.date)
                    putExtra("NOTICE_DESCRIPTION", notice.description)
                    putExtra("NOTICE_CONTENT", notice.content)
                    putExtra("NOTICE_IMAGE", notice.imageUrl)
                }
                itemView.context.startActivity(intent)
            }

            // Show delete button only for admins
            if (isAdmin) {
                btnDeleteNotice.visibility = View.VISIBLE
                btnDeleteNotice.setOnClickListener {
                    Log.d("NoticeAdapter", "Notice ID: ${notice}")
                    // Handle delete notice functionality here
                    // Confirmation dialog
                    AlertDialog.Builder(itemView.context)
                        .setTitle("Delete Notice")
                        .setMessage("Are you sure you want to delete this notice?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // Handle delete notice functionality here
                            val database = MainActivity.getDatabase().reference
                            database.child("Notices").child(notice.id).removeValue().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(itemView.context, "Notice is deleted successfully!", Toast.LENGTH_SHORT).show()
                                    // Notify adapter about item removal
                                    (itemView.context as? AdminNoticeActivity)?.refreshNotices()
                                }
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
            } else {
                btnDeleteNotice.visibility = View.GONE
            }
        }
    }
}
