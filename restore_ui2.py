import os

files = {
    "./app/src/main/java/com/securitynav/security/ui/ProtectedAppsAdapter.kt": """package com.securitynav.security.ui

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
}""",

    "./app/src/main/java/com/securitynav/security/ui/ProtectedAppsFragment.kt": """package com.securitynav.security.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.securitynav.security.R
import com.securitynav.security.data.ProtectedAppInfo
import com.securitynav.security.data.ProtectedAppsManager
import kotlinx.coroutines.launch

class ProtectedAppsFragment : Fragment() {

    private lateinit var manager: ProtectedAppsManager
    private lateinit var adapter: ProtectedAppsAdapter
    private lateinit var tvProtectedSummary: TextView
    private lateinit var pbLoading: ProgressBar
    private lateinit var rvApps: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var etSearch: TextInputEditText

    private var cachedAppList: List<ProtectedAppInfo> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_protected_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manager = ProtectedAppsManager(requireContext())

        tvProtectedSummary = view.findViewById(R.id.tvProtectedSummary)
        pbLoading = view.findViewById(R.id.pbLoadingApps)
        rvApps = view.findViewById(R.id.rvProtectedApps)
        tvEmpty = view.findViewById(R.id.tvEmptyApps)
        etSearch = view.findViewById(R.id.etSearchApp)

        val btnProtectAll = view.findViewById<Button>(R.id.btnProtectAll)
        val btnUnprotectAll = view.findViewById<Button>(R.id.btnUnprotectAll)

        rvApps.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProtectedAppsAdapter(requireContext(), manager) { count ->
            updateSummary(count, cachedAppList.size)
        }
        rvApps.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s?.toString() ?: "")
                checkEmptyState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnProtectAll.setOnClickListener {
            val allPackages = cachedAppList.map { it.packageName }
            manager.protectAll(allPackages)
            adapter.notifyDataSetChanged()
            updateSummary(manager.getProtectedCount(), cachedAppList.size)
        }

        btnUnprotectAll.setOnClickListener {
            manager.unprotectAll()
            adapter.notifyDataSetChanged()
            updateSummary(0, cachedAppList.size)
        }

        loadApps()
    }

    private fun loadApps() {
        pbLoading.visibility = View.VISIBLE
        rvApps.visibility = View.GONE
        tvEmpty.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            cachedAppList = manager.loadAllApps()
            adapter.submitList(cachedAppList)

            pbLoading.visibility = View.GONE
            rvApps.visibility = View.VISIBLE
            updateSummary(manager.getProtectedCount(), cachedAppList.size)
            checkEmptyState()
        }
    }

    private fun updateSummary(protectedCount: Int, totalCount: Int) {
        tvProtectedSummary.text = getString(R.string.protected_summary_format, protectedCount, totalCount)
    }

    private fun checkEmptyState() {
        if (adapter.getFilteredCount() == 0) {
            tvEmpty.visibility = View.VISIBLE
            rvApps.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvApps.visibility = View.VISIBLE
        }
    }
}"""
}

for path, content in files.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(content)
