package com.example.learningandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AddFacilityAdapter(
    private val facilityModelList: List<FacilityModel>,
    private val onItemClick: (FacilityModel) -> Unit
) : RecyclerView.Adapter<AddFacilityAdapter.FacilityViewHolder>() {

    class FacilityViewHolder(itemView: View, val onItemClick: (FacilityModel) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val facilityImage: ImageView = itemView.findViewById(R.id.facilityImage)
        private val facilityTitle: TextView = itemView.findViewById(R.id.facilityTitle)

        fun bind(facility: FacilityModel) {
            Glide.with(itemView.context).load(facility.imageUrl).into(facilityImage)
            facilityTitle.text = facility.title
            itemView.setOnClickListener { onItemClick(facility) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_facility, parent, false)
        return FacilityViewHolder(itemView, onItemClick)
    }

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        holder.bind(facilityModelList[position])
    }

    override fun getItemCount() = facilityModelList.size
}
