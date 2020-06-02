package com.onesignal.outcomes.model

import org.json.JSONException
import org.json.JSONObject

class OSOutcomeEventParams constructor(val outcomeId: String,
                                       val outcomeSource: OSOutcomeSource?, // This field is optional, defaults to zero
                                       var weight: Float, // This field is optional.
                                       var timestamp: Long = 0) {
    @Throws(JSONException::class)
    fun toJSONObject(): JSONObject {
        val json = JSONObject()
                .put(OUTCOME_ID, outcomeId)
        outcomeSource?.let {
            json.put(OUTCOME_SOURCES, it.toJSONObject())
        }
        if (weight > 0) json.put(WEIGHT, weight)
        if (timestamp > 0) json.put(TIMESTAMP, timestamp)
        return json
    }

    fun isUnattributed() = outcomeSource == null || outcomeSource.directBody == null && outcomeSource.indirectBody == null

    override fun toString(): String {
        return "OSOutcomeEventParams{" +
                "outcomeId='" + outcomeId + '\'' +
                ", outcomeSource=" + outcomeSource +
                ", weight=" + weight +
                ", timestamp=" + timestamp +
                '}'
    }

    companion object {
        private const val OUTCOME_ID = "id"
        private const val OUTCOME_SOURCES = "sources"
        private const val WEIGHT = "weight"
        private const val TIMESTAMP = "timestamp"
    }
}