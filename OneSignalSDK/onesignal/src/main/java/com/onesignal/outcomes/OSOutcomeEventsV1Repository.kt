package com.onesignal.outcomes

import com.onesignal.OSLogger
import com.onesignal.OneSignalApiResponseHandler
import com.onesignal.OutcomeEvent
import com.onesignal.influence.model.OSInfluenceType
import com.onesignal.outcomes.domain.OutcomeEventsService
import com.onesignal.outcomes.model.OSOutcomeEventParams
import org.json.JSONException

internal class OSOutcomeEventsV1Repository(logger: OSLogger,
                                           outcomeEventsCache: OSOutcomeEventsCache,
                                           outcomeEventsService: OutcomeEventsService) : OSOutcomeEventsRepository(logger, outcomeEventsCache, outcomeEventsService) {
    override fun requestMeasureOutcomeEvent(appId: String, deviceType: Int, eventParams: OSOutcomeEventParams, responseHandler: OneSignalApiResponseHandler) {
        val event = OutcomeEvent.fromOutcomeEventParamsV2toOutcomeEventV1(eventParams)
        when (event.session) {
            OSInfluenceType.DIRECT -> requestMeasureDirectOutcomeEvent(appId, deviceType, event, responseHandler)
            OSInfluenceType.INDIRECT -> requestMeasureIndirectOutcomeEvent(appId, deviceType, event, responseHandler)
            OSInfluenceType.UNATTRIBUTED -> requestMeasureUnattributedOutcomeEvent(appId, deviceType, event, responseHandler)
            else -> {
            }
        }
    }

    private fun requestMeasureDirectOutcomeEvent(appId: String, deviceType: Int, event: OutcomeEvent, responseHandler: OneSignalApiResponseHandler) {
        try {
            event.toJSONObjectForMeasure()
                    .put(APP_ID, appId)
                    .put(DEVICE_TYPE, deviceType)
                    .put(DIRECT, true)
                    .also { jsonObject ->
                        outcomeEventsService.sendOutcomeEvent(jsonObject, responseHandler)
                    }
        } catch (e: JSONException) {
            logger.error("Generating direct outcome:JSON Failed.", e)
        }
    }

    private fun requestMeasureIndirectOutcomeEvent(appId: String, deviceType: Int, event: OutcomeEvent, responseHandler: OneSignalApiResponseHandler) {
        try {
            event.toJSONObjectForMeasure()
                    .put(APP_ID, appId)
                    .put(DEVICE_TYPE, deviceType)
                    .put(DIRECT, false)
                    .also { jsonObject ->
                        outcomeEventsService.sendOutcomeEvent(jsonObject, responseHandler)
                    }
        } catch (e: JSONException) {
            logger.error("Generating indirect outcome:JSON Failed.", e)
        }
    }

    private fun requestMeasureUnattributedOutcomeEvent(appId: String, deviceType: Int, event: OutcomeEvent, responseHandler: OneSignalApiResponseHandler) {
        try {
            event.toJSONObjectForMeasure()
                    .put(APP_ID, appId)
                    .put(DEVICE_TYPE, deviceType)
                    .also { jsonObject ->
                        outcomeEventsService.sendOutcomeEvent(jsonObject, responseHandler)
                    }
        } catch (e: JSONException) {
            logger.error("Generating unattributed outcome:JSON Failed.", e)
        }
    }

    companion object {
        private const val DIRECT = "direct"
    }
}