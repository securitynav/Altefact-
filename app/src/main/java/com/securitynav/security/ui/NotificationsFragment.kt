package com.securitynav.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.securitynav.security.R
import com.securitynav.security.data.db.SecurityDatabaseHelper
import com.securitynav.security.data.db.SecurityEvent

class NotificationsFragment : Fragment() {

    private lateinit var dbHelper: SecurityDatabaseHelper
    // Use a hardcoded passphrase for demo. In production, derive this securely or ask user.
    private val DB_PASSPHRASE: String by lazy { com.securitynav.security.data.security.KeyStoreManager(requireContext()).getMasterPassphrase() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dbHelper = SecurityDatabaseHelper(requireContext())
        val rv = view.findViewById<RecyclerView>(R.id.rvNotifications)
        rv.layoutManager = LinearLayoutManager(requireContext())
        
        loadNotifications(rv)
    }

    private fun loadNotifications(rv: RecyclerView) {
        try {
            val events = dbHelper.getAllEvents(DB_PASSPHRASE)
            rv.adapter = NotificationsAdapter(events)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class NotificationsAdapter(private val events: List<SecurityEvent>) : 
        RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvType: TextView = view.findViewById(R.id.tvNotifType)
            val tvDesc: TextView = view.findViewById(R.id.tvNotifDesc)
            val tvTime: TextView = view.findViewById(R.id.tvNotifTime)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notification, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val event = events[position]
            holder.tvType.text = event.type
            holder.tvDesc.text = event.description
            holder.tvTime.text = event.timestamp
            
            if (event.type.contains("Leak") || event.type.contains("Fuga")) {
                holder.tvType.setTextColor(android.graphics.Color.parseColor("#F28B82")) // Error Red
            } else {
                holder.tvType.setTextColor(android.graphics.Color.parseColor("#FDE293")) // Warning Yellow
            }
        }

        override fun getItemCount() = events.size
    }
}
