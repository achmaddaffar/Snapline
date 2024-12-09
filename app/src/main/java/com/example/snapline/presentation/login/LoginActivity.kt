package com.example.snapline.presentation.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.snapline.R
import com.example.snapline.databinding.ActivityLoginBinding
import com.example.snapline.presentation.home.HomeActivity
import com.example.snapline.presentation.register.RegisterActivity
import com.example.snapline.util.Helper.isValidEmail
import com.example.snapline.util.Helper.isValidPassword
import com.example.snapline.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialog: Dialog
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
        collectData()
    }

    private fun playAnimation() {
        val greeting = ObjectAnimator.ofFloat(binding.tvGreeting, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(binding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(binding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.llRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                greeting,
                email,
                emailInput,
                password,
                passwordInput,
                login,
                register
            )
            start()
        }
    }

    private fun setupAction() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))

        binding.apply {
            edtLoginEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                    showLoginInvalid(false)
                }

                override fun afterTextChanged(s: Editable?) {
                    edtLoginEmail.error = if (edtLoginEmail.text.toString()
                            .isEmpty()
                    ) getString(R.string.error_field_cannot_be_blank)
                    else if (!s.isValidEmail()) getString(R.string.error_invalid_email)
                    else null
                }
            })

            edtLoginPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                    showLoginInvalid(false)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            btnLogin.setOnClickListener {
                binding.let {
                    val email = it.edtLoginEmail.text.toString()
                    val password = it.edtLoginPassword.text.toString()
                    viewModel.login(email, password)
                }
            }

            tvRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showLoginInvalid(isError: Boolean) {
        binding.cvLoginInvalid.visibility = if (isError) View.VISIBLE else View.GONE
    }

    private fun setButtonEnable() {
        val checkEmail = binding.edtLoginEmail.text
        val checkPassword = binding.edtLoginPassword.text

        binding.btnLogin.isEnabled =
            checkEmail != null && checkEmail.toString().isNotEmpty() && checkEmail.isValidEmail() &&
                    checkPassword != null && checkPassword.toString().isNotEmpty() &&
                    checkPassword.toString().isValidPassword()
    }


    private fun collectData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loginLoading.collect { isLoading ->
                        if (isLoading) dialog.show()
                        else dialog.cancel()
                    }
                }

                launch {
                    viewModel.loginError.collect {
                        showLoginInvalid(true)
                        when (it) {
                            is UiText.DynamicString -> {
                                binding.tvError.text = it.value
                            }

                            is UiText.StringResource -> {
                                binding.tvError.text = getString(it.id)
                            }
                        }
                    }
                }

                launch {
                    viewModel.loginData.collect {
                        if (it?.error == false) {
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}