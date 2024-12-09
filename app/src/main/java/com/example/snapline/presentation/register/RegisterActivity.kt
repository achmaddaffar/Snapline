package com.example.snapline.presentation.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.snapline.R
import com.example.snapline.databinding.ActivityRegisterBinding
import com.example.snapline.presentation.login.LoginActivity
import com.example.snapline.util.Helper.isValidEmail
import com.example.snapline.util.Helper.isValidPassword
import com.example.snapline.util.UiText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dialog: Dialog
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupAction()
        collectState()
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.registerError.collect {
                        when (it) {
                            is UiText.DynamicString -> {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    it.value,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is UiText.StringResource -> {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    getString(it.id),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.registerLoading.collect { isLoading ->
                        showLoading(isLoading)
                    }
                }

                launch {
                    viewModel.registerData.collectLatest { result ->
                        result?.let {
                            if (result.error == true) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    it.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@collectLatest
                            }

                            MaterialAlertDialogBuilder(
                                this@RegisterActivity,
                                R.style.CustomAlertDialogTheme
                            ).apply {
                                setTitle(getString(R.string.registered))
                                setMessage(getString(R.string.account_created_successfully))
                                setCancelable(false)
                                setPositiveButton(getString(R.string.sign_in)) { _, _ ->
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupAction() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))

        binding.apply {
            edRegisterName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                    edRegisterName.error = if (edRegisterName.text.toString()
                            .isEmpty()
                    ) getString(R.string.error_field_cannot_be_blank)
                    else null
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {
                    edRegisterName.error = if (edRegisterName.text.toString()
                            .isEmpty()
                    ) getString(R.string.error_field_cannot_be_blank)
                    else null
                }
            })

            edRegisterEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                    edRegisterEmail.error = if (edRegisterEmail.text.toString()
                            .isEmpty()
                    ) getString(R.string.error_field_cannot_be_blank)
                    else if (!s.isValidEmail()) getString(R.string.error_invalid_email)
                    else null
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {
                    edRegisterEmail.error = if (edRegisterEmail.text.toString()
                            .isEmpty()
                    ) getString(R.string.error_field_cannot_be_blank)
                    else if (!s.isValidEmail()) getString(R.string.error_invalid_email)
                    else null
                }
            })

            edRegisterPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edRegisterConfirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            btnRegister.setOnClickListener {
                if (edRegisterPassword.text.toString() != edRegisterConfirmPassword.text.toString()) {
                    val errorText = getString(
                        R.string.both_password_must_match
                    )
                    edRegisterPassword.setError(errorText, null)
                    edRegisterConfirmPassword.setError(errorText, null)
                    return@setOnClickListener
                }

                binding.let {
                    val name = it.edRegisterName.text.toString()
                    val email = it.edRegisterEmail.text.toString()
                    val password = it.edRegisterPassword.text.toString()
                    viewModel.register(
                        name = name,
                        email = email,
                        password = password
                    )
                }
            }

            tvLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun playAnimation() {
        val greeting = ObjectAnimator.ofFloat(binding.tvGreeting, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val nameInput = ObjectAnimator.ofFloat(binding.tilName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val confirmPassword =
            ObjectAnimator.ofFloat(binding.tvConfirmPassword, View.ALPHA, 1f).setDuration(500)
        val confirmPasswordInput =
            ObjectAnimator.ofFloat(binding.tilConfirmPassword, View.ALPHA, 1f).setDuration(500)
        val register =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.llLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                greeting,
                name,
                nameInput,
                email,
                emailInput,
                password,
                passwordInput,
                confirmPassword,
                confirmPasswordInput,
                register,
                login
            )
        }.start()
    }

    private fun setButtonEnable() {
        val checkName = binding.edRegisterName.text
        val checkEmail = binding.edRegisterEmail.text
        val checkPassword = binding.edRegisterPassword.text
        val checkConfirmPassword = binding.edRegisterConfirmPassword.text
        binding.btnRegister.isEnabled = checkName != null && checkName.toString().isNotEmpty() &&
                checkEmail != null && checkEmail.toString()
            .isNotEmpty() && checkEmail.isValidEmail() &&
                checkPassword != null && checkPassword.toString()
            .isNotEmpty() && checkPassword.toString().isValidPassword() &&
                checkConfirmPassword != null && checkConfirmPassword.toString()
            .isNotEmpty() && checkConfirmPassword.toString().isValidPassword()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) dialog.show() else dialog.cancel()
    }
}