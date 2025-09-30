package net.ommoks.azza.android.app.passonnotifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.SmsManager
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotiListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        if (sbn.packageName == SAMSUNG_MESSAGE) {
            Log.d(TAG, "Handle onNotificationPosted() : package name = ${sbn.packageName}")
            val title = sbn.notification?.extras?.getString(Notification.EXTRA_TITLE, "") ?: ""
            val text = sbn.notification?.extras?.getString(Notification.EXTRA_TEXT, "") ?: ""
            val timestamp = sbn.postTime

            GlobalScope.launch {
                val filter = checkFiltersAndRules(title, text)
                if (filter != null) {
                    val content = Utils.dateTimeFromMillSec(timestamp) +
                            " : " + title + " (" + filter.name + ")"
                    Utils.writeToInternalFile(
                        context = applicationContext,
                        Constants.LOG_FILE,
                        content,
                        append = true
                    )
                    passOnNotification(filter.passOnTo, title, text)
                }
            }
        }
    }

    private fun checkFiltersAndRules(title: String, text: String) : Filter? {
        val filters = Utils.loadFilters(applicationContext)
        filters.map { it -> it as Filter }.forEach { filter ->
            val allMatched = filter.rules.isNotEmpty()
                    && filter.rules.stream().allMatch { rule ->
                        when (rule.type) {
                            RuleType.TitleContains -> title.contains(rule.phrase)
                            RuleType.TitleIs -> title == rule.phrase
                            RuleType.TextContains -> text.contains(rule.phrase)
                            RuleType.TextNotContains -> !text.contains(rule.phrase)
                        }
                    }

            if (allMatched) {
                return filter
            }
        }
        return null
    }

    private fun passOnNotification(phoneNumber: String, title: String, fullText: String) {
        val smsManager: SmsManager = applicationContext.getSystemService(SmsManager::class.java)
        val messageToSend = title + "\n" + fullText
        smsManager.sendMultipartTextMessage(
            phoneNumber,
            null,
            smsManager.divideMessage(messageToSend),
            null,
            null
        )
    }

    companion object {
        private const val TAG = "NotiListener"

        private const val SAMSUNG_MESSAGE = "com.samsung.android.messaging"
    }
}
