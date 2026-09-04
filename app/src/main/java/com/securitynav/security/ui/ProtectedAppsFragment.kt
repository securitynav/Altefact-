package com.securitynav.security.ui

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
}