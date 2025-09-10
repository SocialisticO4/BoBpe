package com.phonepe.app

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var fabQrScanner: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupBottomNavigation()
        setupFabQrScanner()
        
        // Set History as default selected (matching the screenshot)
        bottomNavigation.selectedItemId = R.id.nav_history
        loadFragment(HistoryFragment())
    }

    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        fabQrScanner = findViewById(R.id.fab_qr_scanner)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.nav_alerts -> {
                    loadFragment(AlertsFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.nav_placeholder -> {
                    // This is handled by the FAB, do nothing
                    false
                }
                else -> false
            }
        }
    }

    private fun setupFabQrScanner() {
        fabQrScanner.setOnClickListener {
            // Add animation effect
            it.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                }
                .start()

            // Open QR Scanner
            openQRScanner()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun openQRScanner() {
        // TODO: Implement QR Scanner functionality
        // You can integrate with a QR scanning library like ZXing
        // For now, show a placeholder
        loadFragment(QRScannerFragment())
    }
}