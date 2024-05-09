import android.content.Intent
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
import com.example.gestioninterim.userAnonyme.MainAnonymeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class SignUpJobSeekerFragment : Fragment() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var nationalityEditText: EditText
    private lateinit var birthDateEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var cvEditText: EditText
    private lateinit var commentsEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginButton: TextView
    private lateinit var anonymousButton: TextView


    private val auth = FirebaseAuth.getInstance()
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up_job_seeker, container, false)

        firstNameEditText = view.findViewById(R.id.firstNameEditText)
        lastNameEditText = view.findViewById(R.id.lastNameEditText)
        nationalityEditText = view.findViewById(R.id.nationalityEditText)
        birthDateEditText = view.findViewById(R.id.birthDateEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        cityEditText = view.findViewById(R.id.cityEditText)
        cvEditText = view.findViewById(R.id.cvEditText)
        commentsEditText = view.findViewById(R.id.commentsEditText)
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

    private fun signUp() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val nationality = nationalityEditText.text.toString().trim()
        val birthDate = birthDateEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val city = cityEditText.text.toString().trim()
        val cv = cvEditText.text.toString().trim()
        val comments = commentsEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Vérifier les données obligatoires
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
            return
        }

        // Créer un nouvel utilisateur avec Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Authentification réussie
                    val user = auth.currentUser
                    val userId = user?.uid

                    // Enregistrer les détails de l'utilisateur dans la base de données
                    userId?.let {
                        val userData = HashMap<String, Any>()
                        userData["firstName"] = firstName
                        userData["lastName"] = lastName
                        userData["nationality"] = nationality
                        userData["birthDate"] = birthDate
                        userData["phone"] = phone
                        userData["email"] = email
                        userData["city"] = city
                        userData["cv"] = cv
                        userData["comments"] = comments
                        userData["role"] = "JOB_SEEKER"

                        databaseReference.child(userId).setValue(userData)
                            .addOnSuccessListener {
                                // Succès de l'inscription
                                Toast.makeText(requireContext(), "Inscription réussie", Toast.LENGTH_SHORT).show()
                                // Effacer les champs
                                clearFields()
                                // Rediriger vers l'écran de connexion
                                val loginFragment = LoginFragment()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, loginFragment)
                                    .addToBackStack(null)
                                    .commit()
                            }
                            .addOnFailureListener { e ->
                                // Gérer les erreurs lors de l'inscription
                                Toast.makeText(requireContext(), "Erreur lors de l'inscription: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Gérer les erreurs lors de la création de l'utilisateur
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(requireContext(), "L'utilisateur avec cette adresse e-mail existe déjà.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Erreur lors de l'inscription: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun clearFields() {
        firstNameEditText.text.clear()
        lastNameEditText.text.clear()
        nationalityEditText.text.clear()
        birthDateEditText.text.clear()
        phoneEditText.text.clear()
        emailEditText.text.clear()
        passwordEditText.text.clear()
        cityEditText.text.clear()
        cvEditText.text.clear()
        commentsEditText.text.clear()
    }
}
