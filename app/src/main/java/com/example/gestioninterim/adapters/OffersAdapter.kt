package com.example.gestioninterim.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.OfferModel

class OffersAdapter(
    private val context: Context,
    private val offersList: List<OfferModel>,
    private val onOfferClickListener: OnOfferClickListener
) : RecyclerView.Adapter<OffersAdapter.ViewHolder>() {

    interface OnOfferClickListener {
        fun onOfferClick(offer: OfferModel)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val jobTitleTextView: TextView = itemView.findViewById(R.id.jobTitleTextView)
        val jobDescriptionTextView: TextView = itemView.findViewById(R.id.jobDescriptionTextView)
        val jobTargetTextView: TextView = itemView.findViewById(R.id.jobTargetTextView)
        val periodTextView: TextView = itemView.findViewById(R.id.periodTextView)
        val salaryTextView: TextView = itemView.findViewById(R.id.salaryTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        //val latitudeTextView: TextView = itemView.findViewById(R.id.latitudeTextView)
        //val longitudeTextView: TextView = itemView.findViewById(R.id.longitudeTextView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val offer = offersList[position]
                onOfferClickListener.onOfferClick(offer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.offer_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentOffer = offersList[position]
        holder.jobTitleTextView.text = currentOffer.jobTitle
        holder.jobDescriptionTextView.text = currentOffer.jobDescription
        holder.jobTargetTextView.text = currentOffer.jobTarget
        holder.salaryTextView.text = currentOffer.salary.toString()
        holder.periodTextView.text = currentOffer.period
        holder.locationTextView.text = currentOffer.location
        //holder.latitudeTextView.text = currentOffer.latitude.toString()
        //holder.longitudeTextView.text = currentOffer.longitude.toString()
    }

    override fun getItemCount(): Int {
        return offersList.size
    }
}
