package com.dicoding.todoapp.setting

import android.Manifest
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Notifications permission granted")
            } else {
                showToast("Notifications will not show without permission")
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                val channelName = getString(R.string.notify_channel_name)
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
                if (newValue as Boolean){
                    reminder(channelName)
                } else {
                    cancelReminder()
                }
                true
            }

        }

        private fun cancelReminder() {
            WorkManager.getInstance(requireContext())
                .cancelUniqueWork(NotificationWorker::class.java.simpleName)
        }

        private fun reminder(channelName: String) {
            val dataChannelName = androidx.work.Data.Builder()
                .putString(NOTIFICATION_CHANNEL_ID, channelName)
                .build()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .build()

            val request = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInputData(dataChannelName)
                .build()

            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                NotificationWorker::class.java.simpleName,
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }
}