package com.example.gestioninterim.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.models.UserModel
import com.example.gestioninterim.userEmployeur.MainEmployeurActivity
import com.example.gestioninterim.userInscrit.MainJobSeekerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class LoginFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialiser la référence à la base de données Firebase
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            loginUser()
        }

        return view
    }

    private val auth = FirebaseAuth.getInstance()

    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Vérifier si l'email et le mot de passe ne sont pas vides
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Veuillez saisir votre email et mot de passe",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Authentifier l'utilisateur avec Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Authentification réussie
                    Toast.makeText(requireContext(), "Connexion réussie", Toast.LENGTH_SHORT).show()
                    val currentUser = auth.currentUser
                    val userId = currentUser?.uid

                    if (userId != null) {
                        // Récupérer les données de l'utilisateur depuis la base de données Firebase
                        val databaseReference =
                            FirebaseDatabase.getInstance().reference.child("users").child(userId)
                        databaseReference.addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val user = dataSnapshot.getValue(UserModel::class.java)
                                if (user != null) {
                                    val userRole = user.role
                                    if (userRole == "JOB_SEEKER") {
                                        // Rediriger vers MainJobSeekerActivity
                                        val jobSeekerIntent = Intent(
                                            requireContext(),
                                            MainJobSeekerActivity::class.java
                                        )
                                        startActivity(jobSeekerIntent)
                                    } else if (userRole == "EMPLOYER") {
                                        // Rediriger vers MainEmployeurActivity
                                        val employerIntent = Intent(
                                            requireContext(),
                                            MainEmployeurActivity::class.java
                                        )
                                        startActivity(employerIntent)
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Gérer les erreurs de base de données
                                Toast.makeText(
                                    requireContext(),
                                    "Erreur de base de données: ${databaseError.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                } else {
                    // Authentification échouée
                    Toast.makeText(
                        requireContext(),
                        "Adresse email ou mot de passe incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
