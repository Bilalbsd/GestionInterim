package com.example.gestioninterim

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.models.CandidatureModel
import com.example.gestioninterim.models.OfferModel
import com.example.gestioninterim.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CandidateToOfferFragment : Fragment() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var nationalityEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var cvEditText: EditText
    private lateinit var commentsEditText: EditText
    private lateinit var applyButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var userReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_candidate_to_offer, container, false)

        firstNameEditText = view.findViewById(R.id.firstNameEditText)
        lastNameEditText = view.findViewById(R.id.lastNameEditText)
        nationalityEditText = view.findViewById(R.id.nationalityEditText)
        birthDateEditText = view.findViewById(R.id.birthDateEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        cityEditText = view.findViewById(R.id.cityEditText)
        cvEditText = view.findViewById(R.id.cvEditText)
        commentsEditText = view.findViewById(R.id.commentsEditText)
        applyButton = view.findViewById(R.id.applyButton)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Firebase Realtime Database reference
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser?.uid ?: "")

        // Load user data
        loadUserData()

        val offer = arguments?.getParcelable<OfferModel>("offer")
        if (offer != null) {
            applyButton.setOnClickListener {
                applyToOffer(offer)
            }
        }

        return view
    }

    private fun loadUserData() {
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserModel::class.java)
                user?.let {
                    firstNameEditText.setText(it.firstName)
                    lastNameEditText.setText(it.lastName)
                    nationalityEditText.setText(it.nationality)
                    birthDateEditText.setText(it.birthDate)
                    phoneEditText.setText(it.phone)
                    emailEditText.setText(it.email)
                    cityEditText.setText(it.city)
                    cvEditText.setText(it.cv)
                    commentsEditText.setText(it.comments)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                println("Database error: ${databaseError.message}")
            }
        })
    }

    private fun applyToOffer(offer: OfferModel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("candidatures")

        // Créer un nouvel ID de candidature
        val candidatureId = databaseReference.push().key

        if (candidatureId != null) {
            // Créer un objet CandidatureModel avec les données requises
            val candidatureData = CandidatureModel(
                candidatureId,
                userId ?: "",
                offer.offerId ?: "",
                offer.creatorId ?: "",
                offer.jobTitle ?: "",
                offer.location ?: "",
                "Sans réponse",
                "En cours",
            )

            // Enregistrer la candidature dans la base de données
            databaseReference.child(candidatureId).setValue(candidatureData)
                .addOnSuccessListener {
                    // Candidature réussie
                    Log.d("Candidature", "Candidature réussie pour l'offre: $offer")
                    Toast.makeText(
                        requireContext(),
                        "Candidature envoyée pour l'offre : $offer",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    // Gérer les erreurs lors de la candidature
                    Log.e("Candidature", "Erreur lors de la candidature pour l'offre: $offer", e)
                    Toast.makeText(
                        requireContext(),
                        "Erreur d'envoi de la candidature...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Log.e("Candidature", "Erreur lors de la génération de l'ID de candidature")
            Toast.makeText(
                requireContext(),
                "Erreur lors de l'envoi de la candidature...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        fun newInstance(offer: OfferModel): CandidateToOfferFragment {
            val fragment = CandidateToOfferFragment()
            val args = Bundle()
            args.putParcelable("offer", offer)
            Log.d("OFFER", offer.toString())
            fragment.arguments = args
            return fragment
        }
    }
}
