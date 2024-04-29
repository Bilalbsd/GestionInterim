package com.example.gestioninterim.userAnonyme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapters.OffersAdapter
import com.example.gestioninterim.models.OfferModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListOffersFragment : Fragment(), OffersAdapter.OnOfferClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var offersAdapter: OffersAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var offersList: MutableList<OfferModel>
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_offers, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewOffers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        offersList = mutableListOf()
        offersAdapter = OffersAdapter(requireContext(), offersList, this)
        recyclerView.adapter = offersAdapter

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("jobOffers")

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Load offers from Firebase Realtime Database
        loadOffersFromDatabase()

        return view
    }

    private fun loadOffersFromDatabase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offersList.clear()
                for (postSnapshot in snapshot.children) {
                    val offer = postSnapshot.getValue(OfferModel::class.java)
                    offer?.let { offersList.add(it) }
                }
                offersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                println("Database error: ${error.message}")
            }
        })
    }

    override fun onOfferClick(offer: OfferModel) {
        val user = auth.currentUser
        if (user != null) {
            getUserRole { userRole ->
                if (userRole == "JOB_SEEKER") {
                    // Redirect to fragment showing offer details and apply button
                    val offerDetailFragment = OfferDetailFragment.newInstance(offer)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, offerDetailFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    // Redirect to fragment showing offer details without apply button
                    val offerDetailFragment =
                        OfferDetailFragment.newInstance(offer, showApplyButton = false)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, offerDetailFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        } else {
            // Redirect to fragment showing offer details without apply button (user not logged in)
            val offerDetailFragment =
                OfferDetailFragment.newInstance(offer, showApplyButton = false)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, offerDetailFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getUserRole(callback: (String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId ?: "")

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userRole = dataSnapshot.child("role").value?.toString()
                callback(userRole)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                println("Database error: ${databaseError.message}")
                callback(null)
            }
        })
    }
}
