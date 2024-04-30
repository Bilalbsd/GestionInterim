package com.example.gestioninterim.userAnonyme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.CandidateToOfferFragment
import com.example.gestioninterim.R
import com.example.gestioninterim.models.OfferModel
import com.example.gestioninterim.userEmployeur.ModifyOfferFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class OfferDetailFragment : Fragment(), OnMapReadyCallback {

    private lateinit var offer: OfferModel
    private var showApplyButton: Boolean = false

    private lateinit var mapView: MapView

    companion object {
        private const val ARG_OFFER = "offer"
        private const val ARG_SHOW_APPLY_BUTTON = "show_apply_button"

        fun newInstance(offer: OfferModel, showApplyButton: Boolean = true): OfferDetailFragment {
            val fragment = OfferDetailFragment()
            val args = Bundle()
            args.putParcelable(ARG_OFFER, offer)
            args.putBoolean(ARG_SHOW_APPLY_BUTTON, showApplyButton)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_offer_detail, container, false)

        arguments?.let {
            offer = it.getParcelable(ARG_OFFER) ?: OfferModel()
            showApplyButton = it.getBoolean(ARG_SHOW_APPLY_BUTTON, false)
        }

        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val salaryTextView: TextView = view.findViewById(R.id.salaryTextView)
        val periodTextView: TextView = view.findViewById(R.id.periodTextView)
        val locationTextView: TextView = view.findViewById(R.id.locationTextView)

        val modifyButton: Button = view.findViewById(R.id.modifyButton)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)

        val applyButton: Button = view.findViewById(R.id.applyButton)
        mapView = view.findViewById(R.id.mapView)

        titleTextView.text = offer.jobTitle
        descriptionTextView.text = "Description de l'offre : " + offer.jobDescription
        salaryTextView.text = "Salaire : " + offer.salary.toString()
        periodTextView.text = "Période du poste : " + offer.period
        locationTextView.text = "Localisation : " + offer.location

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (showApplyButton && currentUserId != offer.creatorId) {
            applyButton.visibility = View.VISIBLE
            applyButton.setOnClickListener {
                navigateToCandidateToOfferFragment(offer)
            }
        } else {
            applyButton.visibility = View.GONE
        }

        modifyButton.visibility = View.GONE
        deleteButton.visibility = View.GONE


        // Check if the current user is authenticated and is the creator of the offer
        if (currentUserId != null && currentUserId == offer.creatorId) {
            // User is authenticated and is the creator, show Modify and Delete buttons
            modifyButton.visibility = View.VISIBLE
            modifyButton.setOnClickListener {
                // Handle modify button click
                // Navigate to ModifyOfferFragment passing the offer details
                val modifyOfferFragment = ModifyOfferFragment.newInstance(offer)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, modifyOfferFragment)
                    .addToBackStack(null)
                    .commit()
            }

            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                // Handle delete button click
                // Delete the offer from the database
                deleteOfferFromDatabase(offer.offerId)
            }
        }



        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return view
    }

    private fun deleteOfferFromDatabase(offerId: String?) {
        // Check if offerId is not null
        offerId?.let { id ->
            val databaseReference = FirebaseDatabase.getInstance().reference.child("jobOffers").child(id)
            databaseReference.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Offre supprimée avec succès",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Navigate back to previous fragment
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Erreur lors de la suppression de l'offre: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        val location = LatLng(offer.latitude ?: 0.0, offer.longitude ?: 0.0)
        p0.addMarker(MarkerOptions().position(location).title("Offer Location"))
        p0.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun navigateToCandidateToOfferFragment(offer: OfferModel) {
        val fragment = CandidateToOfferFragment.newInstance(offer)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
