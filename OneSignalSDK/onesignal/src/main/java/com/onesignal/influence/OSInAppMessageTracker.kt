package com.onesignal.influence

import com.onesignal.OSLogger
import com.onesignal.influence.model.OSInfluence
import com.onesignal.influence.model.OSInfluenceChannel
import com.onesignal.influence.model.OSInfluenceType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class OSInAppMessageTracker(dataRepository: OSInfluenceDataRepository, logger: OSLogger) : OSChannelTracker(dataRepository, logger) {
    override val idTag: String
        get() = iamIdTag

    override val channelType: OSInfluenceChannel
        get() = OSInfluenceChannel.IAM

    override fun getLastChannelObjectsReceivedByNewId(id: String?): JSONArray {
        var lastChannelObjectReceived: JSONArray
        lastChannelObjectReceived = try {
            lastChannelObjects
        } catch (exception: JSONException) {
            logger.error("Generating IAM tracker getLastChannelObjects JSONObject ", exception)
            return JSONArray()
        }
        // For IAM we handle redisplay, we need to remove duplicates for new influence Id
        // If min sdk is greater than KITKAT we can refactor this logic to removeObject from JSONArray
        try {
            val auxLastChannelObjectReceived = JSONArray()
            for (i in 0 until lastChannelObjectReceived.length()) {
                val objectId = lastChannelObjectReceived.getJSONObject(i).getString(idTag)
                if (id != objectId) {
                    auxLastChannelObjectReceived.put(lastChannelObjectReceived.getJSONObject(i))
                }
            }
            lastChannelObjectReceived = auxLastChannelObjectReceived
        } catch (exception: JSONException) {
            logger.error("Before KITKAT API, Generating tracker lastChannelObjectReceived get JSONObject ", exception)
        }
        return lastChannelObjectReceived
    }

    @get:Throws(JSONException::class)
    override val lastChannelObjects: JSONArray
        get() = dataRepository.lastIAMsReceivedData

    override val channelLimit: Int
        get() = dataRepository.iamLimit

    override val indirectAttributionWindow: Int
        get() = dataRepository.iamIndirectAttributionWindow

    override fun saveChannelObjects(channelObjects: JSONArray) {
        dataRepository.saveIAMs(channelObjects)
    }

    override fun initInfluencedTypeFromCache() {
        influenceType = dataRepository.iamCachedInfluenceType.also {
            if (it.isIndirect()) indirectIds = lastReceivedIds
        }
        logger.debug("OneSignal InAppMessageTracker initInfluencedTypeFromCache: $this")
    }

    override fun addSessionData(jsonObject: JSONObject, influence: OSInfluence) { // In app message don't influence the session
    }

    override fun cacheState() {
        dataRepository.cacheIAMInfluenceType((if (influenceType == null) OSInfluenceType.UNATTRIBUTED else influenceType)!!)
    }

    companion object {
        @JvmField
        val TAG = OSInAppMessageTracker::class.java.canonicalName
        const val iamIdTag = "iam_id"
    }
}