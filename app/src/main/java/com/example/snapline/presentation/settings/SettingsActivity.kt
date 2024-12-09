package com.example.snapline.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.snapline.R
import com.example.snapline.databinding.ActivitySettingsBinding
import com.example.snapline.presentation.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.settings)

        setupAction()
    }

    private fun setupAction() {
        with(binding) {
            btnLogout.setOnClickListener {
                MaterialAlertDialogBuilder(this@SettingsActivity, R.style.CustomAlertDialogTheme).apply {
                    setTitle(getString(R.string.logout))
                    setMessage(getString(R.string.are_you_sure_you_want_to_logout))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.logout()
                        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    setNegativeButton(getString(R.string.no), null)
                }.show()
            }

            btnChangeLanguage.text = Locale.getDefault().language.uppercase()
            btnChangeLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
        }
    }
}