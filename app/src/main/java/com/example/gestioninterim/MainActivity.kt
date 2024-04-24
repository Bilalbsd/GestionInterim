package com.example.gestioninterim

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gestioninterim.register.RegisterActivity
import com.example.gestioninterim.userAnonyme.MainAnonymeActivity

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Demander la permission de localisation
        requestLocationPermission()

        val continueRegisteredButton = findViewById<Button>(R.id.continueRegisteredButton)
        val continueAnonymouslyButton = findViewById<Button>(R.id.continueAnonymouslyButton)

        continueRegisteredButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        continueAnonymouslyButton.setOnClickListener {
            startActivity(Intent(this, MainAnonymeActivity::class.java))
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // La permission de localisation est accordée
                Toast.makeText(this, "Permission de localisation accordée", Toast.LENGTH_SHORT).show()
            } else {
                // La permission de localisation est refusée
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
