package com.onesignal.outcomes.model

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OSOutcomeSourceBody @JvmOverloads constructor(var notificationIds: JSONArray? = JSONArray(), var inAppMessagesIds: JSONArray? = JSONArray()) {

    @Throws(JSONException::class)
    fun toJSONObject(): JSONObject = JSONObject()
            .put(NOTIFICATION_IDS, notificationIds)
            .put(IAM_IDS, inAppMessagesIds)

    override fun toString(): String {
        return "OSOutcomeSourceBody{" +
                "notificationIds=" + notificationIds +
                ", inAppMessagesIds=" + inAppMessagesIds +
                '}'
    }

    companion object {
        private const val NOTIFICATION_IDS = "notification_ids"
        private const val IAM_IDS = "in_app_message_ids"
    }

}