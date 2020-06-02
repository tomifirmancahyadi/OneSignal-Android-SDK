package com.onesignal.outcomes.domain

import com.onesignal.OneSignalApiResponseHandler
import org.json.JSONObject

interface OutcomeEventsService {
    fun sendOutcomeEvent(jsonObject: JSONObject, responseHandler: OneSignalApiResponseHandler)
}