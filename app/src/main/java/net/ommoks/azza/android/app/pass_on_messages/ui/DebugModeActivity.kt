package net.ommoks.azza.android.app.pass_on_messages.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import net.ommoks.azza.android.app.pass_on_messages.R
import net.ommoks.azza.android.app.pass_on_messages.databinding.DebugModeActivityBinding


@AndroidEntryPoint
class DebugModeActivity : AppCompatActivity() {

    private lateinit var binding: DebugModeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DebugModeActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.debug_mode, DebugModeFragment())
                .commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0)
            insets
        }
        supportActionBar?.title = getString(R.string.debug_mode)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class DebugModeFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.debug_mode_preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            val ret = super.onPreferenceTreeClick(preference)
            val newState = preferenceManager.sharedPreferences?.getBoolean("event_logging", false)
            //TODO: Export log file when turning off
            return ret
        }
    }
}