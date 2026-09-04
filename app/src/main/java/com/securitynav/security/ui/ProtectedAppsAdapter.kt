package com.securitynav.security.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.securitynav.security.R
import com.securitynav.security.data.ProtectedAppInfo
import com.securitynav.security.data.ProtectedAppsManager

class ProtectedAppsAdapter(
    private val context: Context,
    private val manager: ProtectedAppsManager,
    private val onProtectionChanged: (Int) -> Unit
) : RecyclerView.Adapter<ProtectedAppsAdapter.AppViewHolder>() {

    private var allApps: List<ProtectedAppInfo> = emptyList()
    private var filteredApps: List<ProtectedAppInfo> = emptyList()
    private var currentQuery: String = ""

    fun submitList(apps: List<ProtectedAppInfo>) {
        allApps = apps
        filter(currentQuery)
    }

    fun filter(query: String) {
        currentQuery = query.lowercase()
        filteredApps = if (currentQuery.isEmpty()) {
            allApps
        } else {
            allApps.filter {
                it.appName.lowercase().contains(currentQuery) ||
                it.packageName.lowercase().contains(currentQuery)
            }
        }
        notifyDataSetChanged()
    }

    fun getFilteredCount(): Int = filteredApps.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_protected_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(filteredApps[position])
    }

    override fun getItemCount(): Int = filteredApps.size

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivAppIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvAppName)
        private val tvPackage: TextView = itemView.findViewById(R.id.tvAppPackage)
        private val tvBadge: TextView = itemView.findViewById(R.id.tvBadge)
        private val switchProtection: MaterialSwitch = itemView.findViewById(R.id.switchProtection)

        fun bind(app: ProtectedAppInfo) {
            tvName.text = app.appName
            tvPackage.text = app.packageName
            
            try {
                val icon = context.packageManager.getApplicationIcon(app.packageName)
                ivIcon.setImageDrawable(icon)
            } catch (e: Exception) {
                ivIcon.setImageResource(android.R.drawable.sym_def_app_icon)
            }

            switchProtection.setOnCheckedChangeListener(null)
            switchProtection.isChecked = app.isProtected
            updateBadge(app.isProtected)

            switchProtection.setOnCheckedChangeListener { _, isChecked ->
                app.isProtected = isChecked
                manager.toggleProtection(app.packageName, isChecked)
                updateBadge(isChecked)
                onProtectionChanged(manager.getProtectedCount())
            }
        }

        private fun updateBadge(isProtected: Boolean) {
            if (isProtected) {
                tvBadge.text = context.getString(R.string.badge_protected)
                tvBadge.setBackgroundResource(R.drawable.bg_badge_secure)
                tvBadge.setTextColor(Color.WHITE)
            } else {
                tvBadge.text = context.getString(R.string.badge_unprotected)
                tvBadge.setBackgroundColor(Color.parseColor("#E0E0E0"))
                tvBadge.setTextColor(Color.parseColor("#757575"))
            }
        }
    }
}