package com.onesignal;

import com.onesignal.influence.OSTrackerFactory;

class OSRemoteParamController {

    private OneSignalRemoteParams.Params remoteParams;

    /**
     * Save RemoteParams result of android_params request
     */
    void saveRemoteParams(OneSignalRemoteParams.Params remoteParams,
                          OSTrackerFactory trackerFactory,
                          OSSharedPreferences preferences,
                          OSLogger logger) {
        this.remoteParams = remoteParams;

        OneSignalPrefs.saveBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_GT_FIREBASE_TRACKING_ENABLED,
                remoteParams.firebaseAnalytics
        );
        OneSignalPrefs.saveBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_OS_RESTORE_TTL_FILTER,
                remoteParams.restoreTTLFilter
        );
        OneSignalPrefs.saveBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_OS_CLEAR_GROUP_SUMMARY_CLICK,
                remoteParams.clearGroupOnSummaryClick
        );
        OneSignalPrefs.saveBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_OS_RECEIVE_RECEIPTS_ENABLED,
                remoteParams.receiveReceiptEnabled
        );
        OneSignalPrefs.saveBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_OS_UNSUBSCRIBE_WHEN_NOTIFICATIONS_DISABLED,
                remoteParams.unsubscribeWhenNotificationsDisabled
        );
        OneSignalPrefs.saveBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_OS_DISABLE_GMS_MISSING_PROMPT,
                remoteParams.disableGMSMissingPrompt
        );
        OneSignalPrefs.saveBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                preferences.getOutcomesV2KeyName(),
                remoteParams.influenceParams.outcomesV2ServiceEnabled
        );

        logger.debug("OneSignal saveInfluenceParams: " + remoteParams.influenceParams.toString());
        trackerFactory.saveInfluenceParams(remoteParams.influenceParams);
        if (remoteParams.locationShared != null)
            OneSignal.setSharedLocation(remoteParams.locationShared);

        if (remoteParams.requiresUserPrivacyConsent != null) {
            OneSignal.setUserPrivacyConsentEnabled(remoteParams.requiresUserPrivacyConsent);
            // No privacy consent needed, continue with delayed init
//            if (!remoteParams.requiresUserPrivacyConsent)
//                OneSignal.initWithDelayParams();
        }
    }

    OneSignalRemoteParams.Params getRemoteParams() {
        return remoteParams;
    }

    void clearRemoteParams() {
        remoteParams = null;
    }

    boolean getFirebaseAnalyticsEnabled() {
        return OneSignalPrefs.getBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_GT_FIREBASE_TRACKING_ENABLED,
                false);
    }

    boolean getClearGroupSummaryClick() {
        return OneSignalPrefs.getBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_OS_CLEAR_GROUP_SUMMARY_CLICK,
                true);
    }

    boolean unsubscribeWhenNotificationsAreDisabled() {
        return OneSignalPrefs.getBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_OS_UNSUBSCRIBE_WHEN_NOTIFICATIONS_DISABLED,
                false);
    }

    boolean isGMSMissingPromptDisable() {
        return OneSignalPrefs.getBool(
                OneSignalPrefs.PREFS_ONESIGNAL,
                OneSignalPrefs.PREFS_OS_DISABLE_GMS_MISSING_PROMPT,
                false);
    }
}
