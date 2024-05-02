package com.example.gestioninterim.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.CandidatureModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CandidaturesAdapter(
    private val context: Context,
    private val candidaturesList: List<CandidatureModel>,
    private val onCandidatureClickListener: OnCandidatureClickListener
) : RecyclerView.Adapter<CandidaturesAdapter.ViewHolder>() {

    interface OnCandidatureClickListener {
        fun onCandidatureClick(candidature: CandidatureModel)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val jobTitleTextView: TextView = itemView.findViewById(R.id.jobTitleTextView)
        val jobLocationTextView: TextView = itemView.findViewById(R.id.jobLocationTextView)
        val employerResponseTextView: TextView = itemView.findViewById(R.id.employerResponseTextView)
        val jobSeekerResponseTextView: TextView = itemView.findViewById(R.id.jobSeekerResponseTextView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val candidature = candidaturesList[position]
                onCandidatureClickListener.onCandidatureClick(candidature)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_candidature, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCandidature = candidaturesList[position]

        // Récupérer les détails de l'offre associée à l'ID de l'offre dans la candidature
        val offerReference = currentCandidature.offerId?.let {
            FirebaseDatabase.getInstance().reference.child("jobOffers").child(
                it
            )
        }
        if (offerReference != null) {
            offerReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val jobTitle = dataSnapshot.child("jobTitle").getValue(String::class.java)
                    val location = dataSnapshot.child("location").getValue(String::class.java)

                    // Mettre à jour les TextView avec les informations récupérées
                    holder.jobTitleTextView.text = jobTitle
                    holder.jobLocationTextView.text = location
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Gérer l'erreur de base de données
                }
            })
        }

        // Mettre à jour les autres TextView avec les données de la candidature
        holder.jobTitleTextView.text = currentCandidature.jobTitle
        holder.jobLocationTextView.text = currentCandidature.jobLocation
        holder.employerResponseTextView.text = "Employeur : " + currentCandidature.employerResponse
        holder.jobSeekerResponseTextView.text = "Candidat : " + currentCandidature.jobSeekerResponse
    }


    override fun getItemCount(): Int {
        return candidaturesList.size
    }
}
