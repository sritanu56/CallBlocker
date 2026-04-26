package com.callblocker.app.data

import android.content.Context
import androidx.room.*

@TypeConverters(Converters::class)
@Database(
    entities = [BlockRule::class, BlockedCall::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockRuleDao(): BlockRuleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "call_blocker_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

/** Tells Room how to store enums as text in the database. */
class Converters {
    @TypeConverter fun fromRuleType(value: RuleType): String = value.name
    @TypeConverter fun toRuleType(value: String): RuleType = RuleType.valueOf(value)
}
