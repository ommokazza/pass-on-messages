package net.ommoks.azza.android.app.passonnotifications.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import net.ommoks.azza.android.app.passonnotifications.R
import net.ommoks.azza.android.app.passonnotifications.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    this,
                    R.string.sms_permission_denied,
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        supportActionBar?.title = getString(R.string.app_name)
    }

    override fun onResume() {
        super.onResume()

        checkPermissions()
        checkNotificationAccessGranted()
        if (needIgnoreBatteryOptimization()) {
            startBatteryOptimizationActivityIfNeed()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                // Explain to the user why you need the permission
                // You can show a dialog here before requesting the permission
                Toast.makeText(this, "This app needs SMS permission to send messages.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
            else {
                // Directly request the permission
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
        }
    }

    private fun checkNotificationAccessGranted() {
        val notiManager : NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (!notiManager.isNotificationListenerAccessGranted(
                ComponentName(
                    this,
                    NotiListener::class.java
                )
            )) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    private fun needIgnoreBatteryOptimization(): Boolean {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        val packageName = packageName
        return !powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    @SuppressLint("BatteryLife")
    private fun startBatteryOptimizationActivityIfNeed() {
        startActivity(
            Intent(
                Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                parsePackageName(applicationContext)
            )
        )
    }

    private fun parsePackageName(context: Context): Uri {
        return ("package:" + context.packageName).toUri()
    }
}