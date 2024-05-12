import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.models.CandidatureModel
import com.google.firebase.database.*

class AcceptedOffersDetailsEmployerFragment : Fragment() {

    companion object {
        private const val ARG_CANDIDATURE = "candidature"

        fun newInstance(candidature: CandidatureModel): AcceptedOffersDetailsEmployerFragment {
            val fragment = AcceptedOffersDetailsEmployerFragment()
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

    private fun getJobSeekerPhoneNumber(candidateId: String) {
        val userReference =
            FirebaseDatabase.getInstance().reference.child("users").child(candidateId)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                val jobSeekerPhoneNumber =
                    userSnapshot.child("phone").getValue(String::class.java)
                //Log.d("phoneJobSeeker", jobSeekerPhoneNumber.toString())
                if (!jobSeekerPhoneNumber.isNullOrEmpty()) {
                    // Create intent to open phone app with pre-filled number
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$jobSeekerPhoneNumber")
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Job seeker's phone number not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    requireContext(),
                    "Failed to retrieve job seeker's phone number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_accepted_offers_details_employer, container, false)

        // Initialize Views
        val offerTitleTextView: TextView = view.findViewById(R.id.offerTitleTextView)
        val contactButton: Button = view.findViewById(R.id.contactButton)

        // Populate Views with candidature details
        offerTitleTextView.text = candidature.employerResponse

        // Listener for Contact Button
        contactButton.setOnClickListener {
            // Get the recruiter's phone number
            val offerId = candidature.offerId
            if (!offerId.isNullOrEmpty()) {
                candidature.candidateId?.let { it1 -> getJobSeekerPhoneNumber(it1) }
            } else {
                Toast.makeText(requireContext(), "Offer ID is null", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }





}
