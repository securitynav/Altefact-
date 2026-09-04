package com.securitynav.security.ui

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
}