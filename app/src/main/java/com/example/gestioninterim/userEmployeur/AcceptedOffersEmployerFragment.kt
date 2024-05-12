package com.example.gestioninterim.userInscrit

import AcceptedOffersDetailsEmployerFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapters.CandidaturesAdapter
import com.example.gestioninterim.models.CandidatureModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AcceptedOffersEmployerFragment : Fragment(), CandidaturesAdapter.OnCandidatureClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CandidaturesAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_accepted_offers_employer, container, false)

        // Initialize Views
        recyclerView = view.findViewById(R.id.recyclerViewAcceptedOffers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().reference.child("candidatures")

        // Get and display accepted offers
        displayAcceptedCandidatures()

        return view
    }

    private fun displayAcceptedCandidatures() {
        // Listen for changes in the database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val acceptedCandidaturesList = mutableListOf<CandidatureModel>()

                for (candidatureSnapshot in dataSnapshot.children) {
                    val candidature = candidatureSnapshot.getValue(CandidatureModel::class.java)
                    candidature?.let {
                        // Check if the candidature belongs to the current user and meets the criteria
                        if (isAcceptedCandidature(it)) {
                            acceptedCandidaturesList.add(it)
                        }
                    }
                }

                // Set up the adapter with the accepted candidatures list
                adapter = CandidaturesAdapter(requireContext(), acceptedCandidaturesList, this@AcceptedOffersEmployerFragment)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    requireContext(),
                    "Database error: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onCandidatureClick(candidature: CandidatureModel) {
        // Handle click on candidature
        // Navigate to AcceptedOfferDetailFragment passing the selected candidature
        val acceptedOffersDetailsEmployerFragment = AcceptedOffersDetailsEmployerFragment.newInstance(candidature)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, acceptedOffersDetailsEmployerFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun isAcceptedCandidature(candidature: CandidatureModel): Boolean {
        // Check if the candidature belongs to the current user and meets the criteria
        return candidature.employerId == currentUser.uid &&
                candidature.employerResponse == "Accepté" &&
                candidature.jobSeekerResponse == "Accepté"
    }
}
