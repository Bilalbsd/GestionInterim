package com.example.gestioninterim.userAnonyme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapters.OffersAdapter
import com.example.gestioninterim.models.OfferModel
import com.example.gestioninterim.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SearchJobFragment : Fragment(), OffersAdapter.OnOfferClickListener {

    private lateinit var jobTargetSpinner: Spinner
    private lateinit var locationSpinner: Spinner
    private lateinit var periodSpinner: Spinner
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var offersList: MutableList<OfferModel>
    private lateinit var offersAdapter: OffersAdapter
    private lateinit var myOffersOnlyCheckBox: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_job, container, false)

        jobTargetSpinner = view.findViewById(R.id.jobTargetSpinner)
        locationSpinner = view.findViewById(R.id.locationSpinner)
        periodSpinner = view.findViewById(R.id.periodSpinner)
        searchButton = view.findViewById(R.id.searchButton)
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)
        myOffersOnlyCheckBox = view.findViewById(R.id.myOffersOnlyCheckBox)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        offersList = mutableListOf()
        offersAdapter = OffersAdapter(requireContext(), offersList, this)
        recyclerView.adapter = offersAdapter

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("jobOffers")

        // Load distinct jobTarget values into Spinner
        loadJobTargets()
        loadLocationOptions()
        loadPeriodOptions()

        searchButton.setOnClickListener {
            val selectedJobTarget = jobTargetSpinner.selectedItem.toString()
            val selectedLocation = locationSpinner.selectedItem.toString()
            val selectedPeriod = periodSpinner.selectedItem.toString()
            searchJob(selectedJobTarget, selectedLocation, selectedPeriod)
        }

        myOffersOnlyCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showOffersByCreatorId()
            } else {
                offersList.clear()
                offersAdapter.notifyDataSetChanged()
            }
        }

        return view
    }

    private fun loadJobTargets() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobTargets = mutableListOf<String>()
                for (postSnapshot in snapshot.children) {
                    val jobTarget = postSnapshot.child("jobTitle").value.toString()
                    if (jobTarget !in jobTargets) {
                        jobTargets.add(jobTarget)
                    }
                }
                // Populate Spinner with distinct jobTarget values
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jobTargets)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                jobTargetSpinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                println("Database error: ${error.message}")
            }
        })
    }

    private fun loadLocationOptions() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val locations = mutableListOf<String>()
                for (postSnapshot in snapshot.children) {
                    val location = postSnapshot.child("location").value.toString()
                    if (location !in locations) {
                        locations.add(location)
                    }
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                locationSpinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })
    }

    private fun loadPeriodOptions() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val periods = mutableListOf<String>()
                for (postSnapshot in snapshot.children) {
                    val period = postSnapshot.child("period").value.toString()
                    if (period !in periods) {
                        periods.add(period)
                    }
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periods)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                periodSpinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error: ${error.message}")
            }
        })
    }


    private fun searchJob(jobTarget: String, location: String, period: String) {
        val query: Query = databaseReference.orderByChild("jobTarget").equalTo(jobTarget)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offersList.clear()
                for (postSnapshot in snapshot.children) {
                    val offer = postSnapshot.getValue(OfferModel::class.java)
                    offer?.let {
                        // Vérifier si l'offre correspond aux critères de recherche sur la localisation et la période
                        if (it.location == location && it.period == period) {
                            offersList.add(it)
                        }
                    }
                }
                offersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                println("Database error: ${error.message}")
            }
        })
    }


    private fun showOffersByCreatorId() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val userId = it.uid
            val query: Query = databaseReference.orderByChild("creatorId").equalTo(userId)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    offersList.clear()
                    for (postSnapshot in snapshot.children) {
                        val offer = postSnapshot.getValue(OfferModel::class.java)
                        offer?.let {
                            offersList.add(it)
                        }
                    }
                    offersAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    println("Database error: ${error.message}")
                }
            })
        }
    }

    override fun onOfferClick(offer: OfferModel) {
        // Créer un fragment pour afficher les détails de l'offre et passer l'offre en tant qu'argument
        val offerDetailFragment = OfferDetailFragment.newInstance(offer)

        // Remplacer le fragment actuel par le fragment des détails de l'offre
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, offerDetailFragment)
            .addToBackStack(null)
            .commit()
    }

}
