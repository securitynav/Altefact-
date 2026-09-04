import os

files = {
    "./app/src/main/res/layout/item_vault_record.xml": """<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="8dp"
    app:cardBackgroundColor="@color/surface_light"
    app:cardCornerRadius="12dp"
    app:strokeColor="@color/google_btn_stroke"
    app:strokeWidth="1dp"
    app:cardElevation="0dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <TextView
                android:id="@+id/tvRecordTitle"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Título del Secreto"
                android:textColor="@color/text_primary"
                android:textSize="14sp"
                android:textStyle="bold" />

            <TextView
                android:id="@+id/tvRecordAlgorithm"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="@color/secondary"
                android:textSize="10sp"
                android:textStyle="bold"
                android:background="@drawable/bg_badge_secure"
                android:paddingHorizontal="6dp"
                android:paddingVertical="2dp" />
        </LinearLayout>

        <TextView
            android:id="@+id/tvRecordCiphertext"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@color/text_secondary"
            android:fontFamily="monospace"
            android:textSize="11sp"
            android:maxLines="2"
            android:ellipsize="end"
            android:layout_marginTop="4dp" />

        <TextView
            android:id="@+id/tvDecryptedText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="@color/primary"
            android:textSize="12sp"
            android:textStyle="bold"
            android:layout_marginTop="4dp"
            android:visibility="gone" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical"
            android:layout_marginTop="8dp">

            <TextView
                android:id="@+id/tvRecordTimestamp"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:textColor="@color/text_secondary"
                android:textSize="10sp" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnDecryptRecord"
                style="?attr/materialButtonOutlinedStyle"
                android:layout_width="wrap_content"
                android:layout_height="36dp"
                android:text="Descifrar"
                android:textSize="11sp"
                android:textColor="@color/primary"
                app:strokeColor="@color/primary"
                android:layout_marginEnd="6dp" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnDeleteRecord"
                style="?attr/materialButtonOutlinedStyle"
                android:layout_width="wrap_content"
                android:layout_height="36dp"
                android:text="Borrar"
                android:textSize="11sp"
                android:textColor="@color/error"
                app:strokeColor="@color/error" />
        </LinearLayout>

    </LinearLayout>
</com.google.android.material.card.MaterialCardView>""",

    "./app/src/main/java/com/securitynav/security/ui/VaultAdapter.kt": """package com.securitynav.security.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.securitynav.security.R
import com.securitynav.security.data.EncryptedVaultItem
import com.securitynav.security.data.EncryptionManager

class VaultAdapter(
    private val encryptionManager: EncryptionManager,
    private val onDeleteClicked: (EncryptedVaultItem) -> Unit
) : RecyclerView.Adapter<VaultAdapter.VaultViewHolder>() {

    private var items: List<EncryptedVaultItem> = emptyList()

    fun submitList(newItems: List<EncryptedVaultItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vault_record, parent, false)
        return VaultViewHolder(view)
    }

    override fun onBindViewHolder(holder: VaultViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvRecordTitle)
        private val tvAlgorithm: TextView = itemView.findViewById(R.id.tvRecordAlgorithm)
        private val tvCiphertext: TextView = itemView.findViewById(R.id.tvRecordCiphertext)
        private val tvDecrypted: TextView = itemView.findViewById(R.id.tvDecryptedText)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvRecordTimestamp)
        private val btnDecrypt: Button = itemView.findViewById(R.id.btnDecryptRecord)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDeleteRecord)

        private var isDecrypted = false

        fun bind(item: EncryptedVaultItem) {
            tvTitle.text = item.title
            tvAlgorithm.text = if (item.algorithm.contains("AES")) "AES-256" else item.algorithm
            tvCiphertext.text = "Cifrado: ${item.encryptedData}"
            tvTimestamp.text = item.timestamp

            tvDecrypted.visibility = View.GONE
            isDecrypted = false
            btnDecrypt.text = "Descifrar"

            btnDecrypt.setOnClickListener {
                if (!isDecrypted) {
                    val decrypted = encryptionManager.decryptString(item.encryptedData)
                    tvDecrypted.text = "Descifrado: $decrypted"
                    tvDecrypted.visibility = View.VISIBLE
                    btnDecrypt.text = "Ocultar"
                    isDecrypted = true
                } else {
                    tvDecrypted.visibility = View.GONE
                    btnDecrypt.text = "Descifrar"
                    isDecrypted = false
                }
            }

            btnDelete.setOnClickListener {
                onDeleteClicked(item)
            }
        }
    }
}""",
}

for path, content in files.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(content)
