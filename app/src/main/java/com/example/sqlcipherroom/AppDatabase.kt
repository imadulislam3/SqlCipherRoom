package com.example.sqlcipherroom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory

@Database(entities = [Person::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        @Volatile
        private var personDB: AppDatabase? = null
        @Volatile
        private var personDBSecure: AppDatabase? = null
        @Volatile
        private var personDBSecureWithMemorySecurity: AppDatabase? = null

        fun getInstance(
            context: Context,
            secure: Boolean = false,
            memorySecure: Boolean = false
        ): AppDatabase {
            return if (secure) {
                if(!memorySecure) {
                    personDBSecure ?: synchronized(this) {
                        personDBSecure ?: buildDatabase(context, secure, memorySecure).also { personDBSecure = it }
                    }
                } else {
                    personDBSecureWithMemorySecurity ?: synchronized(this) {
                        personDBSecureWithMemorySecurity ?: buildDatabase(context, secure, memorySecure).also { personDBSecureWithMemorySecurity = it }
                    }
                }
            } else {
                personDB ?: synchronized(this) {
                    personDB ?: buildDatabase(context, secure).also { personDB = it }
                }
            }
        }

        private fun buildDatabase(
            context: Context,
            secure: Boolean,
            memorySecure: Boolean = false
        ): AppDatabase {
            val dbname = if(secure && memorySecure) {
                "encrypted-with-mem"
            } else if(secure && !memorySecure) {
                "encrypted"
            } else {
                "not-encrypted"
            }
            val builder = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "${dbname}.db"
            )
            if (secure) {
                val passphrase: ByteArray =
                    SQLiteDatabase.getBytes("P@s5P4ras3VeryL0n9".toCharArray())
                val factory = SupportFactory(passphrase, object : SQLiteDatabaseHook {
                    override fun preKey(database: SQLiteDatabase?) = Unit

                    override fun postKey(database: SQLiteDatabase?) {
                        if (memorySecure) {
                            database?.rawExecSQL(
                                "PRAGMA cipher_memory_security = ON"
                            )
                        } else {
                            database?.rawExecSQL("PRAGMA cipher_memory_security = OFF")
                        }
                    }
                })
                builder.openHelperFactory(factory)
            }

            return builder.build()
        }
    }
}
