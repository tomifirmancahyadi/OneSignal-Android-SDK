package com.onesignal.outcomes

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.onesignal.influence.model.OSInfluenceChannel

open class OSOutcomeTableProvider {
    /**
     * On the outcome table rename session column to notification influence type
     * Add columns for iam ids and iam influence type
     *
     * @param db
     */
    open fun upgradeOutcomeTableRevision2To3(db: SQLiteDatabase) {
        val commonColumns: String = OutcomeEventsTable.ID + "," +
                OutcomeEventsTable.COLUMN_NAME_NAME + "," +
                OutcomeEventsTable.COLUMN_NAME_TIMESTAMP + "," +
                OutcomeEventsTable.COLUMN_NAME_NOTIFICATION_IDS + "," +
                OutcomeEventsTable.COLUMN_NAME_WEIGHT
        val commonColumnsWithSessionColumn = commonColumns + "," + OutcomeEventsTable.COLUMN_NAME_SESSION
        val commonColumnsWithNewSessionColumn = commonColumns + "," + OutcomeEventsTable.COLUMN_NAME_NOTIFICATION_INFLUENCE_TYPE
        val auxOutcomeTableName = OutcomeEventsTable.TABLE_NAME + "_aux"

        with(db) {
            try {
                // Since SQLite does not support dropping a column we need to:
                // See https://www.techonthenet.com/sqlite/tables/alter_table.php
                //   1. Alter current table
                //   2. Create new table
                //   3. Copy data to new table
                //   4. Drop altered table
                execSQL("BEGIN TRANSACTION;")
                execSQL("ALTER TABLE " + OutcomeEventsTable.TABLE_NAME + " RENAME TO " + auxOutcomeTableName + ";")
                execSQL(sqlCreateOutcomeEntries)
                execSQL("INSERT INTO " + OutcomeEventsTable.TABLE_NAME + "(" + commonColumnsWithNewSessionColumn + ")" +
                        " SELECT " + commonColumnsWithSessionColumn + " FROM " + auxOutcomeTableName + ";")
                execSQL("DROP TABLE $auxOutcomeTableName;")
                execSQL("COMMIT;")
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * On the cache unique outcome table rename table, rename column notification id to influence id
     * Add column channel type
     *
     * @param db
     */
    open fun upgradeCacheOutcomeTableRevision1To2(db: SQLiteDatabase) {
        val commonColumns: String = CachedUniqueOutcomeTable.ID + "," + CachedUniqueOutcomeTable.COLUMN_NAME_NAME
        val commonColumnsWithNotificationIdColumn = commonColumns + "," + CachedUniqueOutcomeTable.COLUMN_NAME_NOTIFICATION_ID
        val commonColumnsWithNewInfluenceIdColumn = commonColumns + "," + CachedUniqueOutcomeTable.COLUMN_CHANNEL_INFLUENCE_ID
        val oldCacheUniqueOutcomeTable = CachedUniqueOutcomeTable.OLD_TABLE_NAME

        with(db) {
            try {
                // Since SQLite does not support dropping a column we need to:
                // See https://www.techonthenet.com/sqlite/tables/alter_table.php
                //   1. Alter current table
                //   2. Create new table
                //   3. Copy data to new table
                //   4. Drop altered table
                execSQL("BEGIN TRANSACTION;")
                execSQL(sqlCreateUniqueOutcomeEntries)
                execSQL("INSERT INTO " + CachedUniqueOutcomeTable.TABLE_NAME + "(" + commonColumnsWithNewInfluenceIdColumn + ")" +
                        " SELECT " + commonColumnsWithNotificationIdColumn + " FROM " + oldCacheUniqueOutcomeTable + ";")
                execSQL("UPDATE " + CachedUniqueOutcomeTable.TABLE_NAME +
                        " SET " + CachedUniqueOutcomeTable.COLUMN_CHANNEL_TYPE + " = \'" + OSInfluenceChannel.NOTIFICATION.toString() + "\';")
                execSQL("DROP TABLE $oldCacheUniqueOutcomeTable;")
                execSQL("COMMIT;")
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Testing mock purposes
     */
    open val sqlCreateOutcomeEntries: String
        get() = SQL_CREATE_OUTCOME_ENTRIES

    /**
     * Testing mock purposes
     */
    open val sqlCreateUniqueOutcomeEntries: String
        get() = SQL_CREATE_UNIQUE_OUTCOME_ENTRIES

    companion object {
        private const val INTEGER_PRIMARY_KEY_TYPE = " INTEGER PRIMARY KEY"
        private const val TEXT_TYPE = " TEXT"
        private const val FLOAT_TYPE = " FLOAT"
        private const val TIMESTAMP_TYPE = " TIMESTAMP"

        const val OUTCOME_EVENT_TABLE = OutcomeEventsTable.TABLE_NAME
        const val CACHE_UNIQUE_OUTCOME_TABLE = CachedUniqueOutcomeTable.TABLE_NAME
        const val CACHE_UNIQUE_OUTCOME_COLUMN_CHANNEL_INFLUENCE_ID = CachedUniqueOutcomeTable.COLUMN_CHANNEL_INFLUENCE_ID
        const val CACHE_UNIQUE_OUTCOME_COLUMN_CHANNEL_TYPE = CachedUniqueOutcomeTable.COLUMN_CHANNEL_TYPE

        private const val SQL_CREATE_OUTCOME_ENTRIES = "CREATE TABLE " + OutcomeEventsTable.TABLE_NAME + " (" +
                OutcomeEventsTable.ID + INTEGER_PRIMARY_KEY_TYPE + "," +
                OutcomeEventsTable.COLUMN_NAME_NOTIFICATION_INFLUENCE_TYPE + TEXT_TYPE + "," +
                OutcomeEventsTable.COLUMN_NAME_IAM_INFLUENCE_TYPE + TEXT_TYPE + "," +
                OutcomeEventsTable.COLUMN_NAME_NOTIFICATION_IDS + TEXT_TYPE + "," +
                OutcomeEventsTable.COLUMN_NAME_IAM_IDS + TEXT_TYPE + "," +
                OutcomeEventsTable.COLUMN_NAME_NAME + TEXT_TYPE + "," +
                OutcomeEventsTable.COLUMN_NAME_TIMESTAMP + TIMESTAMP_TYPE + "," +  // "params TEXT" Added in v4, removed in v5.
                OutcomeEventsTable.COLUMN_NAME_WEIGHT + FLOAT_TYPE +  // New in v5, missing migration added in v6
                ");"
        private const val SQL_CREATE_UNIQUE_OUTCOME_ENTRIES = "CREATE TABLE " + CachedUniqueOutcomeTable.TABLE_NAME + " (" +
                CachedUniqueOutcomeTable.ID + INTEGER_PRIMARY_KEY_TYPE + "," +
                CachedUniqueOutcomeTable.COLUMN_CHANNEL_INFLUENCE_ID + TEXT_TYPE + "," +
                CachedUniqueOutcomeTable.COLUMN_CHANNEL_TYPE + TEXT_TYPE + "," +
                CachedUniqueOutcomeTable.COLUMN_NAME_NAME + TEXT_TYPE +
                ");"
    }
}