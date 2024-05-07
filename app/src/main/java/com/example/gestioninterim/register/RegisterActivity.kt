package com.example.gestioninterim.register

import SignUpJobSeekerFragment
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.gestioninterim.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialisation des fragments
        val signUpJobSeekerFragment = SignUpJobSeekerFragment()
        val signUpEmployerFragment = SignUpEmployerFragment()

        // Affichage du fragment de recherche d'emploi par défaut
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, signUpJobSeekerFragment)
            commit()
        }

        // Gestion des clics sur les boutons
        findViewById<Button>(R.id.jobSeekerButton).setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, signUpJobSeekerFragment)
                commit()
            }
        }

        findViewById<Button>(R.id.employerButton).setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, signUpEmployerFragment)
                commit()
            }
        }
    }
}
