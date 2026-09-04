package com.securitynav.security.data.db

import android.content.ContentValues
import android.content.Context
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SecurityEvent(
    val id: Long,
    val type: String,
    val description: String,
    val timestamp: String
)

class SecurityDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SecurityVault.db"
        const val TABLE_EVENTS = "security_events"
        const val COLUMN_ID = "_id"
        const val COLUMN_TYPE = "type"
        const val COLUMN_DESC = "description"
        const val COLUMN_TIMESTAMP = "timestamp"

        init {
            // Initialize SQLCipher
            System.loadLibrary("sqlcipher")
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_EVENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TYPE TEXT,
                $COLUMN_DESC TEXT,
                $COLUMN_TIMESTAMP TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        onCreate(db)
    }

    fun insertEvent(passphrase: String, type: String, description: String): Long {
        val db = this.getWritableDatabase(passphrase)
        val values = ContentValues().apply {
            put(COLUMN_TYPE, type)
            put(COLUMN_DESC, description)
            put(COLUMN_TIMESTAMP, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        }
        val id = db.insert(TABLE_EVENTS, null, values)
        db.close()
        return id
    }

    fun getAllEvents(passphrase: String): List<SecurityEvent> {
        val events = mutableListOf<SecurityEvent>()
        val db = this.getReadableDatabase(passphrase)
        val cursor = db.query(TABLE_EVENTS, null, null, null, null, null, "$COLUMN_ID DESC")
        
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESC))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                events.add(SecurityEvent(id, type, desc, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return events
    }
}
