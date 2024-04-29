package com.example.gestioninterim.userAnonyme

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.register.RegisterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainAnonymeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_anonyme)

        enableEdgeToEdge()

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
                    true
                }
                R.id.search_nav -> {
                    replaceFragment(SearchJobFragment())
                    true
                }
                R.id.login_nav -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}
