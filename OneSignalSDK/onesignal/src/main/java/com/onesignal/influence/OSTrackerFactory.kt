package com.onesignal.influence

import com.onesignal.OSLogger
import com.onesignal.OSSharedPreferences
import com.onesignal.OneSignal.AppEntryAction
import com.onesignal.OneSignalRemoteParams.InfluenceParams
import com.onesignal.influence.model.OSInfluence
import com.onesignal.influence.model.OSInfluenceChannel
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class OSTrackerFactory(preferences: OSSharedPreferences, logger: OSLogger) {
    private val trackers = ConcurrentHashMap<String, OSChannelTracker>()
    private val dataRepository: OSInfluenceDataRepository = OSInfluenceDataRepository(preferences)

    val influences: List<OSInfluence>
        get() = trackers.values.map { it.currentSessionInfluence }

    val iAMChannelTracker: OSChannelTracker
        get() = trackers[OSInAppMessageTracker.TAG]!!

    val notificationChannelTracker: OSChannelTracker
        get() = trackers[OSNotificationTracker.TAG]!!

    val channels: List<OSChannelTracker>
        get() {
            val channels: MutableList<OSChannelTracker> = mutableListOf()
            notificationChannelTracker?.let { channels.add(it) }
            iAMChannelTracker?.let { channels.add(it) }
            return channels
        }

    init {
        trackers[OSInAppMessageTracker.TAG] = OSInAppMessageTracker(dataRepository, logger)
        trackers[OSNotificationTracker.TAG] = OSNotificationTracker(dataRepository, logger)
    }

    fun initFromCache() {
        trackers.values.forEach {
            it.initInfluencedTypeFromCache()
        }
    }

    fun saveInfluenceParams(influenceParams: InfluenceParams) {
        dataRepository.saveInfluenceParams(influenceParams)
    }

    fun addSessionData(jsonObject: JSONObject, influences: List<OSInfluence>) {
        influences.forEach {
            when (it.influenceChannel) {
                OSInfluenceChannel.NOTIFICATION -> notificationChannelTracker?.addSessionData(jsonObject, it)
                OSInfluenceChannel.IAM -> {
                }
            }
        }
    }

    fun getChannelByEntryAction(entryAction: AppEntryAction): OSChannelTracker? {
        return if (entryAction.isNotificationClick) notificationChannelTracker else null
    }

    fun getChannelsToResetByEntryAction(entryAction: AppEntryAction): List<OSChannelTracker> {
        val channels: MutableList<OSChannelTracker> = ArrayList()
        // Avoid reset session if application is closed
        if (entryAction.isAppClose) return channels
        // Avoid reset session if app was focused due to a notification click (direct session recently set)
        val notificationChannel = if (entryAction.isAppOpen) notificationChannelTracker else null
        notificationChannel?.let {
            channels.add(it)
        }
        iAMChannelTracker?.let {
            channels.add(it)
        }
        return channels
    }
}