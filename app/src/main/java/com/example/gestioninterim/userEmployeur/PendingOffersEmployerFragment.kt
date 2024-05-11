import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapters.CandidaturesAdapter
import com.example.gestioninterim.models.CandidatureModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class PendingOffersEmployerFragment : Fragment(), CandidaturesAdapter.OnCandidatureClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CandidaturesAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending_offers_employer, container, false)

        // Initialize Views
        recyclerView = view.findViewById(R.id.recyclerViewPendingOffers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().reference.child("candidatures")

        // Get and display pending offers
        displayPendingCandidatures()

        return view
    }

    override fun onCandidatureClick(candidature: CandidatureModel) {
        // Handle click on candidature
        // Navigate to AcceptedOfferDetailFragment passing the selected candidature
        val pendingOffersDetailsEmployerFragment = PendingOffersDetailsEmployerFragment.newInstance(candidature)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, pendingOffersDetailsEmployerFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun displayPendingCandidatures() {
        // Listen for changes in the database
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pendingCandidaturesList = mutableListOf<CandidatureModel>()

                for (candidatureSnapshot in dataSnapshot.children) {
                    val candidature = candidatureSnapshot.getValue(CandidatureModel::class.java)
                    candidature?.let {
                        // Check if the candidature belongs to the current user (employer) and meets the criteria
                        if (isPendingCandidature(it)) {
                            pendingCandidaturesList.add(it)
                        }
                    }
                }

                // Set up the adapter with the pending candidatures list
                adapter = CandidaturesAdapter(requireContext(), pendingCandidaturesList, this@PendingOffersEmployerFragment)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    requireContext(),
                    "Database error: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun isPendingCandidature(candidature: CandidatureModel): Boolean {
        // Check if the candidature belongs to the current user (employer) and meets the criteria
        return candidature.employerId == currentUser.uid &&
                candidature.employerResponse == "Sans réponse" &&
                candidature.jobSeekerResponse == "En cours"
    }

    // Function to accept a candidature
    private fun acceptCandidature(candidatureId: String) {
        databaseReference.child(candidatureId).child("employerResponse").setValue("Accepté")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Candidature accepted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to accept candidature", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to reject a candidature
    private fun rejectCandidature(candidatureId: String) {
        databaseReference.child(candidatureId).child("employerResponse").setValue("Refusé")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Candidature rejected successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to reject candidature", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to respond to a candidature
    private fun respondToCandidature(candidatureId: String, response: String) {
        databaseReference.child(candidatureId).child("employerResponse").setValue(response)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Response sent successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to send response", Toast.LENGTH_SHORT).show()
            }
    }
}
