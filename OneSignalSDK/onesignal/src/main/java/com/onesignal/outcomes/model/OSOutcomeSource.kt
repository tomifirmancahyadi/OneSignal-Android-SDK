package com.onesignal.outcomes.model

import org.json.JSONException
import org.json.JSONObject

class OSOutcomeSource(var directBody: OSOutcomeSourceBody?,
                      var indirectBody: OSOutcomeSourceBody?) {
    @Throws(JSONException::class)
    fun toJSONObject(): JSONObject {
        val json = JSONObject()
        directBody?.let {
            json.put(DIRECT, it.toJSONObject())
        }
        indirectBody?.let {
            json.put(INDIRECT, it.toJSONObject())
        }
        return json
    }

    fun setDirectBody(directBody: OSOutcomeSourceBody?) = this.apply {
        this.directBody = directBody
    }

    fun setIndirectBody(indirectBody: OSOutcomeSourceBody?) = this.apply {
        this.indirectBody = indirectBody
    }

    override fun toString(): String {
        return "OSOutcomeSource{" +
                "directBody=" + directBody +
                ", indirectBody=" + indirectBody +
                '}'
    }

    companion object {
        private const val DIRECT = "direct"
        private const val INDIRECT = "indirect"
    }
}