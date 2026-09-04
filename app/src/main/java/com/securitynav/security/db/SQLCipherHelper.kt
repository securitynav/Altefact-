package com.securitynav.security.db

import android.content.Context
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class SQLCipherHelper(context: Context) : SQLiteOpenHelper(context, "security_secure.db", null, 1) {
    init {
        SQLiteDatabase.loadLibs(context)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS security_events (id TEXT PRIMARY KEY, event_type TEXT, description TEXT, timestamp INTEGER);")
        db.execSQL("CREATE TABLE IF NOT EXISTS traffic_packets (packet_id TEXT PRIMARY KEY, source_ip TEXT, destination_ip TEXT, source_port INTEGER, destination_port INTEGER, protocol TEXT, payload TEXT);")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS security_events")
        db.execSQL("DROP TABLE IF EXISTS traffic_packets")
        onCreate(db)
    }
}
