package com.example.gestioninterim.userEmployeur

import com.example.gestioninterim.userAnonyme.ListOffersFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.models.OfferModel
import com.example.gestioninterim.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateOfferFragment : Fragment() {

    private lateinit var editTextJobTitle: EditText
    private lateinit var editTextJobDescription: EditText
    private lateinit var editTextJobTarget: EditText
    private lateinit var editTextPeriod: EditText
    private lateinit var editTextSalary: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var editTextLatitude: EditText
    private lateinit var editTextLongitude: EditText
    private lateinit var buttonAddOffer: Button

    // Database reference
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_offer, container, false)

        // Initialize Views
        editTextJobTitle = view.findViewById(R.id.editTextJobTitle)
        editTextJobDescription = view.findViewById(R.id.editTextJobDescription)
        editTextJobTarget = view.findViewById(R.id.editTextJobTarget)
        editTextPeriod = view.findViewById(R.id.editTextPeriod)
        editTextSalary = view.findViewById(R.id.editTextSalary)
        editTextLocation = view.findViewById(R.id.editTextLocation)
        editTextLatitude = view.findViewById(R.id.editTextLatitude)
        editTextLongitude = view.findViewById(R.id.editTextLongitude)
        buttonAddOffer = view.findViewById(R.id.buttonAddOffer)

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("jobOffers")

        // Handle button click
        buttonAddOffer.setOnClickListener {
            addJobOfferToDatabase()
        }

        return view
    }

    private fun addJobOfferToDatabase() {
        // Récupérer l'ID de l'utilisateur actuel
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Create a Map object to represent the offer data
            val offerData = OfferModel(
                editTextJobTitle.text.toString(),
                editTextJobDescription.text.toString(),
                editTextJobTarget.text.toString(),
                editTextSalary.text.toString().toDouble(),
                editTextPeriod.text.toString(),
                editTextLocation.text.toString(),
                editTextLatitude.text.toString().toDouble(),
                editTextLongitude.text.toString().toDouble(),
                creatorId = userId // Ajouter l'ID de l'utilisateur actuel comme créateur de l'offre
            )

            // Push offer data to the "jobOffers" node in the Realtime Database
            val newOfferReference = databaseReference.push()
            val offerId = newOfferReference.key // Récupérer l'ID de l'offre nouvellement créée

            if (offerId != null) {
                // Mettre à jour l'objet OfferModel avec l'ID de l'offre nouvellement créée
                offerData.offerId = offerId

                newOfferReference.setValue(offerData)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Offre ajoutée avec succès à la base de données en temps réel",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Redirection vers le fragment Liste d'offres
                        val listOffersFragment = ListOffersFragment()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, listOffersFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    .addOnFailureListener { e ->
                        println("Error adding offer: $e")
                        Toast.makeText(
                            requireContext(),
                            "Erreur lors de l'ajout de l'offre",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                println("Error getting offer ID")
                Toast.makeText(
                    requireContext(),
                    "Erreur lors de la récupération de l'identifiant de l'offre",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            println("User ID is null")
            Toast.makeText(
                requireContext(),
                "Erreur lors de la récupération de l'identifiant de l'utilisateur",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}
