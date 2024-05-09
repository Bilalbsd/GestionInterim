package com.example.gestioninterim.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.register.LoginFragment
import com.example.gestioninterim.register.RegisterActivity
import com.example.gestioninterim.userAnonyme.MainAnonymeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent

class SignUpEmployerFragment : Fragment() {

    private lateinit var companyNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var linksEditText: EditText
    private lateinit var signUpButton: Button

    private lateinit var loginButton: TextView
    private lateinit var anonymousButton: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up_employer, container, false)

        companyNameEditText = view.findViewById(R.id.companyNameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        addressEditText = view.findViewById(R.id.addressEditText)
        linksEditText = view.findViewById(R.id.linksEditText)
        signUpButton = view.findViewById(R.id.signUpButton)

        loginButton = view.findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .addToBackStack(null)
                .commit()
        }

        anonymousButton = view.findViewById(R.id.anonymousButton)

        anonymousButton.setOnClickListener {
            startActivity(Intent(requireContext(), MainAnonymeActivity::class.java))
        }

        signUpButton.setOnClickListener {
            signUp()
        }

        return view
    }

    private val auth = FirebaseAuth.getInstance()

    private fun signUp() {
        val companyName = companyNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val links = linksEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Vérifier les données obligatoires
        if (companyName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Veuillez remplir les champs obligatoires !",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        fun clearFields() {
            companyNameEditText.text.clear()
            addressEditText.text.clear()
            linksEditText.text.clear()
            passwordEditText.text.clear()
            phoneEditText.text.clear()
            emailEditText.text.clear()
        }

        // Créer l'utilisateur avec Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Récupérer l'ID de l'utilisateur créé
                    val user = auth.currentUser
                    val userId = user?.uid

                    // Enregistrer les détails de l'utilisateur dans la base de données
                    val databaseReference = FirebaseDatabase.getInstance().reference.child("users")
                    val userData = HashMap<String, Any>()
                    userData["companyName"] = companyName
                    userData["email"] = email
                    userData["phone"] = phone
                    userData["address"] = address
                    userData["links"] = links
                    userData["role"] = "EMPLOYER"

                    userId?.let {
                        databaseReference.child(userId).setValue(userData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Inscription réussie",
                                    Toast.LENGTH_SHORT
                                ).show()
                                clearFields()
                                // Vous pouvez rediriger l'utilisateur vers une autre activité ou afficher un message de réussite
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Erreur lors de l'inscription dans la base de données: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    // Gérer les erreurs lors de la création de l'utilisateur
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            requireContext(),
                            "L'utilisateur avec cette adresse e-mail existe déjà.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Erreur lors de l'inscription: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }
}