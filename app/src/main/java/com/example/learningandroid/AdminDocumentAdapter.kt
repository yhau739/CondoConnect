package com.example.learningandroid

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.learningandroid.models.DocumentModel
import com.google.firebase.database.DatabaseReference

class AdminDocumentAdapter(
    private val documents: List<DocumentModel>,
    private val database: DatabaseReference
) : RecyclerView.Adapter<AdminDocumentAdapter.AdminDocumentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminDocumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_item_document, parent, false)
        return AdminDocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminDocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.bind(document)
    }

    override fun getItemCount(): Int = documents.size

    inner class AdminDocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val documentIcon: ImageView = itemView.findViewById(R.id.ivDocumentIcon)
        private val documentTitle: TextView = itemView.findViewById(R.id.tvDocumentTitle)
        private val documentSubtitle: TextView = itemView.findViewById(R.id.tvDocumentSubtitle)
        private val btnDeleteDocument: Button = itemView.findViewById(R.id.btnDeleteDocument)

        fun bind(document: DocumentModel) {
            documentTitle.text = document.title
            documentSubtitle.text = document.subtitle

            // Open PDF document when the card is clicked
            itemView.findViewById<ConstraintLayout>(R.id.documentCardLayout).setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(document.url))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                itemView.context.startActivity(intent)
            }

            btnDeleteDocument.setOnClickListener {
                // Show a confirmation dialog before deleting
                AlertDialog.Builder(itemView.context)
                    .setTitle("Delete Document")
                    .setMessage("Are you sure you want to delete this document?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        deleteDocument(document)
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        private fun deleteDocument(document: DocumentModel) {
            document.id?.let {
                database.child(it).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(itemView.context, "Document deleted successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(itemView.context, "Failed to delete document", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
