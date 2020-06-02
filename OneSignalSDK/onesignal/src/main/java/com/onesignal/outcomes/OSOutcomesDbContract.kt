package com.onesignal.outcomes

import android.provider.BaseColumns

internal open class OutcomeEventsTable {
    companion object {
        const val ID = BaseColumns._ID
        const val TABLE_NAME = "outcome"

        // Influence ids
        const val COLUMN_NAME_NOTIFICATION_IDS = "notification_ids" // OneSignal Notification Ids
        const val COLUMN_NAME_IAM_IDS = "iam_ids" // OneSignal iam Ids

        // Influence type
        const val COLUMN_NAME_SESSION = "session" // Old column name
        const val COLUMN_NAME_NOTIFICATION_INFLUENCE_TYPE = "notification_influence_type"
        const val COLUMN_NAME_IAM_INFLUENCE_TYPE = "iam_influence_type"

        // Outcome data
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_WEIGHT = "weight"
        const val COLUMN_NAME_TIMESTAMP = "timestamp"
    }
}

internal open class CachedUniqueOutcomeTable {
    companion object {
        const val ID = BaseColumns._ID
        const val OLD_TABLE_NAME = "cached_unique_outcome_notification" // Old table name
        const val TABLE_NAME = "cached_unique_outcome"
        const val COLUMN_NAME_NOTIFICATION_ID = "notification_id" // Old column name
        const val COLUMN_CHANNEL_INFLUENCE_ID = "channel_influence_id" // OneSignal Channel influence Id
        const val COLUMN_CHANNEL_TYPE = "channel_type" // OneSignal Channel Type
        const val COLUMN_NAME_NAME = "name"
    }
}