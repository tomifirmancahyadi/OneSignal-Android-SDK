package com.onesignal.influence

import com.onesignal.OSLogger
import com.onesignal.influence.model.OSInfluence
import com.onesignal.influence.model.OSInfluenceChannel
import com.onesignal.influence.model.OSInfluenceType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class OSNotificationTracker(dataRepository: OSInfluenceDataRepository, logger: OSLogger) : OSChannelTracker(dataRepository, logger) {

    override fun getLastChannelObjectsReceivedByNewId(id: String?): JSONArray {
        return try {
            lastChannelObjects
        } catch (exception: JSONException) {
            logger.error("Generating Notification tracker getLastChannelObjects JSONObject ", exception)
            JSONArray()
        }
    }

    @get:Throws(JSONException::class)
    override val lastChannelObjects: JSONArray
        get() = dataRepository.lastNotificationsReceivedData
    override val idTag: String
        get() = notificationIdTag

    override val channelType: OSInfluenceChannel
        get() = OSInfluenceChannel.NOTIFICATION

    override val channelLimit: Int
        get() = dataRepository.notificationLimit

    override val indirectAttributionWindow: Int
        get() = dataRepository.notificationIndirectAttributionWindow

    override fun saveChannelObjects(channelObjects: JSONArray) {
        dataRepository.saveNotifications(channelObjects)
    }

    override fun initInfluencedTypeFromCache() {
        influenceType = dataRepository.notificationCachedInfluenceType.also {
            if (it.isIndirect())
                indirectIds = lastReceivedIds
            else if (it.isDirect())
                directId = dataRepository.cachedNotificationOpenId
        }
        logger.debug("OneSignal NotificationTracker initInfluencedTypeFromCache: $this")
    }

    override fun addSessionData(jsonObject: JSONObject, influence: OSInfluence) {
        if (influence.influenceType.isAttributed()) try {
            jsonObject.put(DIRECT_TAG, influence.influenceType.isDirect())
            jsonObject.put(NOTIFICATIONS_IDS, influence.ids)
        } catch (exception: JSONException) {
            logger.error("Generating notification tracker addSessionData JSONObject ", exception)
        }
    }

    override fun cacheState() {
        dataRepository.cacheNotificationInfluenceType(influenceType ?: OSInfluenceType.UNATTRIBUTED)
        dataRepository.cacheNotificationOpenId(directId)
    }

    companion object {
        @JvmField
        val TAG = OSNotificationTracker::class.java.canonicalName
        private const val DIRECT_TAG = "direct"
        private const val NOTIFICATIONS_IDS = "notification_ids"
        const val notificationIdTag = "notification_id"
    }
}