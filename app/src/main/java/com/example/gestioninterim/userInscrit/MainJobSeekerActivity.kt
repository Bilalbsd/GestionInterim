package com.example.gestioninterim.userInscrit

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gestioninterim.ProfileFragment
import com.example.gestioninterim.userAnonyme.ListOffersFragment
import com.example.gestioninterim.R
import com.example.gestioninterim.userAnonyme.SearchJobFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainJobSeekerActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_job_seeker)

        val listOffersFragment = ListOffersFragment()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, listOffersFragment)
                commit()
            }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_nav -> {
                    replaceFragment(ListOffersFragment())
                }
                R.id.search_nav -> {
                    replaceFragment(SearchJobFragment())
                }
                R.id.profile_nav -> {
                    replaceFragment(ProfileFragment())
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}
