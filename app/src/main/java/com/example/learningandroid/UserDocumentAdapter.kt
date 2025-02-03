package com.example.learningandroid

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.DocumentModel

class UserDocumentAdapter(
    private val documents: List<DocumentModel>
) : RecyclerView.Adapter<UserDocumentAdapter.UserDocumentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDocumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_item_document, parent, false)
        return UserDocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserDocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.bind(document)
    }

    override fun getItemCount(): Int = documents.size

    inner class UserDocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val documentIcon: ImageView = itemView.findViewById(R.id.ivDocumentIcon)
        private val documentTitle: TextView = itemView.findViewById(R.id.tvDocumentTitle)
        private val documentSubtitle: TextView = itemView.findViewById(R.id.tvDocumentSubtitle)
        private val btnDeleteDocument: Button = itemView.findViewById(R.id.btnDeleteDocument)

        fun bind(document: DocumentModel) {
            documentTitle.text = document.title
            documentSubtitle.text = document.subtitle
            btnDeleteDocument.visibility = View.GONE

            // Open PDF document when the card is clicked
            itemView.findViewById<ConstraintLayout>(R.id.documentCardLayout).setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(document.url))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                itemView.context.startActivity(intent)
            }
        }
    }
}
