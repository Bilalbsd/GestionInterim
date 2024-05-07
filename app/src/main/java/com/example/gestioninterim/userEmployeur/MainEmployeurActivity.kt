package com.example.gestioninterim.userEmployeur

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gestioninterim.ProfileEmployerFragment
import com.example.gestioninterim.ProfileFragment
import com.example.gestioninterim.userAnonyme.ListOffersFragment
import com.example.gestioninterim.R
import com.example.gestioninterim.userAnonyme.SearchJobFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainEmployeurActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_employeur)

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
                R.id.create_nav -> {
                    replaceFragment(CreateOfferFragment())
                }
                R.id.profile_nav -> {
                    replaceFragment(ProfileEmployerFragment())
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
