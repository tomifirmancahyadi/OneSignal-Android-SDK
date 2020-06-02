package com.onesignal.outcomes

import com.onesignal.OneSignalAPIClient
import com.onesignal.OneSignalApiResponseHandler
import com.onesignal.outcomes.domain.OutcomeEventsService
import org.json.JSONObject

internal abstract class OSOutcomeEventsClient(val client: OneSignalAPIClient) : OutcomeEventsService {
    abstract override fun sendOutcomeEvent(jsonObject: JSONObject, responseHandler: OneSignalApiResponseHandler)
}