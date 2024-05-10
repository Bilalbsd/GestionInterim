package com.example.gestioninterim.userEmployeur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.models.OfferModel
import com.google.firebase.database.*

class ModifyOfferFragment : Fragment() {

    private lateinit var offer: OfferModel
    private lateinit var editTextJobTitle: EditText
    private lateinit var editTextJobDescription: EditText
    private lateinit var editTextJobTarget: EditText
    private lateinit var editTextPeriod: EditText
    private lateinit var editTextSalary: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var editTextLatitude: EditText
    private lateinit var editTextLongitude: EditText
    private lateinit var buttonUpdateOffer: Button

    companion object {
        private const val ARG_OFFER = "offer"

        fun newInstance(offer: OfferModel): ModifyOfferFragment {
            val fragment = ModifyOfferFragment()
            val args = Bundle()
            args.putParcelable(ARG_OFFER, offer)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_modify_offer, container, false)

        arguments?.let {
            offer = it.getParcelable(ARG_OFFER) ?: OfferModel()
        }

        editTextJobTitle = view.findViewById(R.id.editTextJobTitle)
        editTextJobDescription = view.findViewById(R.id.editTextJobDescription)
        editTextJobTarget = view.findViewById(R.id.editTextJobTarget)
        editTextPeriod = view.findViewById(R.id.editTextPeriod)
        editTextSalary = view.findViewById(R.id.editTextSalary)
        editTextLocation = view.findViewById(R.id.editTextLocation)
        editTextLatitude = view.findViewById(R.id.editTextLatitude)
        editTextLongitude = view.findViewById(R.id.editTextLongitude)
        buttonUpdateOffer = view.findViewById(R.id.updateOfferButton)

        // Set existing offer details to EditText fields
        editTextJobTitle.setText(offer.jobTitle)
        editTextJobDescription.setText(offer.jobDescription)
        editTextJobTarget.setText(offer.jobTarget)
        editTextPeriod.setText(offer.period)
        editTextSalary.setText(offer.salary.toString())
        editTextLocation.setText(offer.location)
        editTextLatitude.setText(offer.latitude.toString())
        editTextLongitude.setText(offer.longitude.toString())

        buttonUpdateOffer.setOnClickListener {
            updateOfferInDatabase()
        }

        return view
    }

    private fun updateOfferInDatabase() {
        // Update offer details
        offer.jobTitle = editTextJobTitle.text.toString()
        offer.jobDescription = editTextJobDescription.text.toString()
        offer.jobTarget = editTextJobTarget.text.toString()
        offer.period = editTextPeriod.text.toString()
        offer.salary = editTextSalary.text.toString().toDouble()
        offer.location = editTextLocation.text.toString()
        offer.latitude = editTextLatitude.text.toString().toDouble()
        offer.longitude = editTextLongitude.text.toString().toDouble()

        // Update offer in the database
        val databaseReference = FirebaseDatabase.getInstance().reference.child("jobOffers").child(offer.offerId ?: "")
        databaseReference.setValue(offer)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Offre mise à jour avec succès", Toast.LENGTH_SHORT).show()
                // Navigate back to previous fragment
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erreur lors de la mise à jour de l'offre: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
