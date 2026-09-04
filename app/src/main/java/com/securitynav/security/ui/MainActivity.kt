package com.securitynav.security.ui

import android.content.Intent

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

import com.google.firebase.auth.FirebaseAuth
import com.securitynav.security.data.security.KeyStoreManager

import com.securitynav.security.billing.SubscriptionManager
import com.securitynav.security.util.OtaUpdateManager
import android.widget.Toast
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.securitynav.security.R

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        
        // Initialize Billing
        
        // Initialize Hardware Key
        try {
            KeyStoreManager(this).getMasterPassphrase()
        } catch(e: Exception) {
            // Ignored, just to initialize
        }

        SubscriptionManager.initGooglePlayBilling(this) {
            // Check if we need to do anything on success
        }

        // Check for OTA Updates
        lifecycleScope.launch {
            val otaManager = OtaUpdateManager(this@MainActivity)
            val updateInfo = otaManager.checkForUpdates()
            if (updateInfo.isUpdateAvailable) {
                Toast.makeText(this@MainActivity, "Actualización disponible: ${updateInfo.versionName}. Descargando...", Toast.LENGTH_LONG).show()
                otaManager.downloadAndInstallApk(updateInfo.downloadUrl) { progress ->
                    // Could update a progress bar here
                }
            }
        }
        
        // Load default fragment

        if (savedInstanceState == null) {
            loadFragment(SecurityHubFragment())
            navView.setCheckedItem(R.id.nav_hub)
        }

        navView.setNavigationItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_hub -> SecurityHubFragment()
                R.id.nav_analytics -> VulnerabilityDashboardFragment()
                R.id.nav_vpn -> VpnFragment()
                R.id.nav_antennas -> CellTowerFragment()
                R.id.nav_guard -> GuardFragment()
                R.id.nav_vault -> VaultFragment()
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_logout -> {
                    // Limpiar llaves en RAM (simulado cerrando la Activity)
                    val intent = Intent(this, PinActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    return@setNavigationItemSelectedListener true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return@setNavigationItemSelectedListener true
                }
                else -> SecurityHubFragment()
            }
            loadFragment(fragment)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
