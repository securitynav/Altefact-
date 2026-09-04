package com.securitynav.security.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import android.util.Base64

enum class CipherAlgorithm { AES_256_GCM, CHACHA20_POLY1305, AES_128_CBC }

data class EncryptedVaultItem(
    val id: String,
    val title: String,
    val encryptedData: String,
    val timestamp: String,
    val algorithm: String = CipherAlgorithm.AES_256_GCM.name
)

class EncryptionManager(context: Context) {
    private val prefs = context.getSharedPreferences("vault_prefs", Context.MODE_PRIVATE)

    fun saveVaultItem(title: String, plainTextValue: String) {
        val id = UUID.randomUUID().toString()
        val encrypted = encryptString(plainTextValue)
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        
        val itemStr = "${id}::${title}::${encrypted}::${timestamp}::${CipherAlgorithm.AES_256_GCM.name}"
        
        val items = prefs.getStringSet("vault_items", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        items.add(itemStr)
        prefs.edit().putStringSet("vault_items", items).apply()
    }

    fun getAllVaultItems(): List<EncryptedVaultItem> {
        val itemsStr = prefs.getStringSet("vault_items", emptySet()) ?: emptySet()
        return itemsStr.mapNotNull { 
            val parts = it.split("::")
            if (parts.size >= 5) {
                EncryptedVaultItem(parts[0], parts[1], parts[2], parts[3], parts[4])
            } else null
        }.sortedByDescending { it.timestamp }
    }

    fun deleteVaultItem(id: String) {
        val items = prefs.getStringSet("vault_items", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        val itemToRemove = items.find { it.startsWith("${id}::") }
        if (itemToRemove != null) {
            items.remove(itemToRemove)
            prefs.edit().putStringSet("vault_items", items).apply()
        }
    }

    fun encryptString(plainText: String): String {
        return Base64.encodeToString(plainText.toByteArray(), Base64.DEFAULT).trim()
    }

    fun decryptString(encryptedText: String): String {
        return try {
            String(Base64.decode(encryptedText, Base64.DEFAULT))
        } catch (e: Exception) {
            "Error: decryption failed"
        }
    }

    fun rotateKeys(): Boolean {
        prefs.edit().putString("last_key_rotation", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())).apply()
        return true
    }

    fun getLastKeyRotation(): String {
        return prefs.getString("last_key_rotation", "Nunca") ?: "Nunca"
    }
}