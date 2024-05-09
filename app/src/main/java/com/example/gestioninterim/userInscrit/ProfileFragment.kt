package com.example.gestioninterim

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gestioninterim.register.RegisterActivity
import com.example.gestioninterim.userEmployeur.MainEmployeurActivity
import com.example.gestioninterim.userInscrit.AcceptedOffersFragment
import com.example.gestioninterim.userInscrit.PendingOffersFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userReference: DatabaseReference
    private lateinit var userId: String

    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var roleTextView: TextView
    private lateinit var pendingCandidaturesButton: Button
    private lateinit var acceptedCandidaturesButton: Button
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""

        userReference = FirebaseDatabase.getInstance().reference.child("users").child(userId)

        firstNameTextView = view.findViewById(R.id.firstNameTextView)
        lastNameTextView = view.findViewById(R.id.lastNameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        roleTextView = view.findViewById(R.id.roleTextView)
        pendingCandidaturesButton = view.findViewById(R.id.pendingCandidaturesButton)
        acceptedCandidaturesButton = view.findViewById(R.id.acceptedCandidaturesButton)
        logoutButton = view.findViewById(R.id.logoutButton)

        loadUserData()

        pendingCandidaturesButton.setOnClickListener {
            navigateToPendingCandidaturesFragment()
        }

        acceptedCandidaturesButton.setOnClickListener {
            navigateToAcceptedCandidaturesFragment()
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        return view
    }

    private fun loadUserData() {
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val firstName = dataSnapshot.child("firstName").value?.toString() ?: ""
                val lastName = dataSnapshot.child("lastName").value?.toString() ?: ""
                val email = dataSnapshot.child("email").value?.toString() ?: ""
                val role = dataSnapshot.child("role").value?.toString() ?: ""

                if (role == "EMPLOYER") {
                    val company = dataSnapshot.child("companyName").value?.toString() ?: ""
                    firstNameTextView.text = company
                } else {
                    firstNameTextView.text = firstName
                }
                lastNameTextView.text = lastName
                emailTextView.text = email
                roleTextView.text = role
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                println("Database error: ${databaseError.message}")
            }
        })
    }


    private fun navigateToPendingCandidaturesFragment() {
        val fragment = PendingOffersFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToAcceptedCandidaturesFragment() {
        val fragment = AcceptedOffersFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun logoutUser() {
        auth.signOut()
        startActivity(Intent(requireContext(), RegisterActivity::class.java))
    }
}
