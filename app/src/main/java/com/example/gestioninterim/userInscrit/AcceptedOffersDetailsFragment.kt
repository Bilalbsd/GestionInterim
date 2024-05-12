package com.example.gestioninterim.userInscrit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.models.CandidatureModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AcceptedOffersDetailFragment : Fragment() {

    companion object {
        private const val ARG_CANDIDATURE = "candidature"

        fun newInstance(candidature: CandidatureModel): AcceptedOffersDetailFragment {
            val fragment = AcceptedOffersDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_CANDIDATURE, candidature)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var candidature: CandidatureModel
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            candidature = it.getParcelable(ARG_CANDIDATURE)!!
        }
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("candidatures")
    }

    private fun getRecruiterPhoneNumber(offerId: String) {
        val offerReference =
            FirebaseDatabase.getInstance().reference.child("jobOffers").child(offerId)
        offerReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val creatorId = dataSnapshot.child("creatorId").getValue(String::class.java)
                if (creatorId != null) {
                    val userReference =
                        FirebaseDatabase.getInstance().reference.child("users").child(creatorId)
                    userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            val recruiterPhoneNumber =
                                userSnapshot.child("phone").getValue(String::class.java)
                            if (!recruiterPhoneNumber.isNullOrEmpty()) {
                                // Create intent to open phone app with pre-filled number
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$recruiterPhoneNumber")
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Recruiter's phone number not available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle database error
                            Toast.makeText(
                                requireContext(),
                                "Failed to retrieve recruiter's phone number",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), "Creator ID is null", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    requireContext(),
                    "Failed to retrieve offer details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showDirectionsToWorkplace(offerId: String) {
        val offerReference = FirebaseDatabase.getInstance().reference.child("jobOffers").child(offerId)
        offerReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val location = dataSnapshot.child("location").getValue(String::class.java)
                if (!location.isNullOrEmpty()) {
                    // Create intent to open Google Maps with directions to workplace
                    val uri = "http://maps.google.com/maps?daddr=$location"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Workplace location not available", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(requireContext(), "Failed to retrieve offer details", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_accepted_offers_details, container, false)

        // Initialize Views
        val offerTitleTextView: TextView = view.findViewById(R.id.offerTitleTextView)
        val acceptButton: Button = view.findViewById(R.id.acceptButton)
        val rejectButton: Button = view.findViewById(R.id.rejectButton)
        val contactButton: Button = view.findViewById(R.id.contactButton)
        val itinaryButton: Button = view.findViewById(R.id.itinaryButton)

        // Populate Views with candidature details
        offerTitleTextView.text = candidature.employerResponse

        // Listener for Accept Button
        acceptButton.setOnClickListener {
            // Update jobSeekerResponse to "Accepté" in the database
            updateJobSeekerResponse("Accepté")
        }

        // Listener for Reject Button
        rejectButton.setOnClickListener {
            // Update jobSeekerResponse to "Refusé" in the database
            updateJobSeekerResponse("Refusé")
        }

        // Listener for Contact Button
        contactButton.setOnClickListener {
            // Get the recruiter's phone number
            val offerId = candidature.offerId
            if (!offerId.isNullOrEmpty()) {
                getRecruiterPhoneNumber(offerId)
            } else {
                Toast.makeText(requireContext(), "Offer ID is null", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener pour le bouton Itinéraire
        itinaryButton.setOnClickListener {
            val offerId = candidature.offerId
            if (!offerId.isNullOrEmpty()) {
                showDirectionsToWorkplace(offerId)
            } else {
                Toast.makeText(requireContext(), "Offer ID is null", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun updateJobSeekerResponse(response: String) {
        // Check if candidatureId is not null
        val candidatureId = candidature.candidatureId
        if (candidatureId != null) {
            // Update jobSeekerResponse in the database
            databaseReference.child(candidatureId).child("jobSeekerResponse").setValue(response)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Response updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update response", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Candidature ID is null", Toast.LENGTH_SHORT).show()
        }
    }
}
