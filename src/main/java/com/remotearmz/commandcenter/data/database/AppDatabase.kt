package com.remotearmz.commandcenter.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.remotearmz.commandcenter.data.converter.Converters
import com.remotearmz.commandcenter.data.dao.ClientDao
import com.remotearmz.commandcenter.data.dao.LeadDao
import com.remotearmz.commandcenter.data.dao.OutreachDao
import com.remotearmz.commandcenter.data.entity.ClientEntity
import com.remotearmz.commandcenter.data.entity.LeadEntity
import com.remotearmz.commandcenter.data.entity.OutreachEntity

@Database(
    entities = [ClientEntity::class, LeadEntity::class, OutreachEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun leadDao(): LeadDao
    abstract fun outreachDao(): OutreachDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "command_center_db"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE outreach (
                    id TEXT PRIMARY KEY NOT NULL,
                    clientId TEXT NOT NULL,
                    leadId TEXT,
                    outreachType TEXT NOT NULL,
                    outreachDate INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    notes TEXT,
                    createdAt INTEGER NOT NULL,
                    updatedAt INTEGER NOT NULL
                )")
            }
        }
    }
}
