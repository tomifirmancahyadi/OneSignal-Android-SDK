package com.onesignal.outcomes.model

import com.onesignal.influence.model.OSInfluenceChannel

open class OSCachedUniqueOutcome(private val influenceId: String,
                                 private val channel: OSInfluenceChannel) {
    open fun getInfluenceId() = influenceId
    open fun getChannel() = channel
}