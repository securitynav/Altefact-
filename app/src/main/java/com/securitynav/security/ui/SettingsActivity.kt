package com.securitynav.security.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.securitynav.security.R
import com.securitynav.security.data.AuthManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.securitynav.security.util.OtaUpdateManager
import android.widget.Toast


class SettingsActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        authManager = AuthManager(this)
        
        val freqSpinner = findViewById<Spinner>(R.id.spinnerFrequency)
        val verbSpinner = findViewById<Spinner>(R.id.spinnerVerbosity)
        
        val freqAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("Every 15 mins", "Hourly", "Daily"))
        freqSpinner.adapter = freqAdapter
        
        val verbAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("Low", "Medium", "High (Full Packet)"))
        verbSpinner.adapter = verbAdapter
        
        
        val btnCheckUpdates = findViewById<Button>(R.id.btnCheckUpdates)
        btnCheckUpdates.setOnClickListener {
            Toast.makeText(this, "Buscando actualizaciones en el servidor...", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                try {
                    val otaManager = OtaUpdateManager(this@SettingsActivity)
                    val updateInfo = otaManager.checkForUpdates()
                    if (updateInfo.isUpdateAvailable) {
                        Toast.makeText(this@SettingsActivity, "Actualización: ${updateInfo.versionName}. Descargando...", Toast.LENGTH_LONG).show()
                        otaManager.downloadAndInstallApk(updateInfo.downloadUrl) { progress ->
                            // background download
                        }
                    } else {
                        Toast.makeText(this@SettingsActivity, "Estás en la última versión", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SettingsActivity, "Error conectando al servidor OTA", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            authManager.setLoggedIn(false)
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
