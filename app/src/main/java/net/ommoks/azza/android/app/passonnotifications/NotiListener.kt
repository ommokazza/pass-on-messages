package net.ommoks.azza.android.app.passonnotifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.SmsManager
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.ommoks.azza.android.app.passonnotifications.data.MainRepository
import javax.inject.Inject

@AndroidEntryPoint
class NotiListener : NotificationListenerService() {

    @Inject
    lateinit var mainRepository: MainRepository

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        if (sbn.packageName == SAMSUNG_MESSAGE) {
            Log.d(TAG, "Handle onNotificationPosted() : package name = ${sbn.packageName}")
            val title = sbn.notification?.extras?.getString(Notification.EXTRA_TITLE, "") ?: ""
            val text = sbn.notification?.extras?.getString(Notification.EXTRA_TEXT, "") ?: ""
            val timestamp = sbn.postTime

            GlobalScope.launch {
                getMatchedFilters(title, text).forEach { filter ->
                    val content = Utils.dateTimeFromMillSec(timestamp) +
                            " : " + title + " (" + filter.name + ")"
                    Utils.writeToInternalFile(
                        context = applicationContext,
                        Constants.LOG_FILE,
                        content,
                        append = true
                    )
                    passOnNotification(filter.passOnTo, title, text)
                    delay(1000)
                }
            }
        }
    }

    private suspend fun getMatchedFilters(title: String, text: String) : List<Filter> {
        return mainRepository.loadFilters().filter { it -> it.isMatched(title, text) }
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
