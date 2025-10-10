package net.ommoks.azza.android.app.pass_on_messages.ui

import android.app.Notification
import android.provider.Telephony
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.SmsManager
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.ommoks.azza.android.app.pass_on_messages.common.Constants
import net.ommoks.azza.android.app.pass_on_messages.common.Utils
import net.ommoks.azza.android.app.pass_on_messages.data.MainRepository
import net.ommoks.azza.android.app.pass_on_messages.data.model.Filter
import net.ommoks.azza.android.app.pass_on_messages.data.model.isMatched
import javax.inject.Inject

@AndroidEntryPoint
class NotiListener : NotificationListenerService() {

    @Inject
    lateinit var mainRepository: MainRepository

    private val mutex = Mutex()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        if (sbn.packageName == Telephony.Sms.getDefaultSmsPackage(this)) {
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
                    mutex.withLock {
                        passOnNotification(filter.passOnTo, title, text)
                        mainRepository.updateLastTimestamp(filter, timestamp)
                        delay(3000)
                    }
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
    }
}
