package com.example.snapline.presentation.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.snapline.R
import com.example.snapline.databinding.ActivitySplashBinding
import com.example.snapline.presentation.home.HomeActivity
import com.example.snapline.presentation.login.LoginActivity
import com.example.snapline.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        collectState()
    }

    private fun setupAction() {
        Glide.with(this@SplashActivity)
            .load(R.mipmap.ic_launcher)
            .circleCrop()
            .centerCrop()
            .into(binding.ivSplashlogo)

        AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(binding.ivSplashlogo, View.ALPHA, 1f).setDuration(Constants.SPLASH_SCREEN_DURATION),
                ObjectAnimator.ofFloat(binding.ivSplashlogo, View.TRANSLATION_Y, 200f)
                    .setDuration(800),
                ObjectAnimator.ofFloat(binding.ivSplashlogo, View.TRANSLATION_Y, -10000f)
                    .setDuration(600)
            )
            start()
        }.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(a: Animator) {}

            override fun onAnimationEnd(a: Animator) {
                viewModel.getToken()
            }

            override fun onAnimationCancel(a: Animator) {}

            override fun onAnimationRepeat(a: Animator) {}
        })
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.token.collectLatest {  token ->
                        val intent = if (token.isNullOrEmpty()) {
                            Intent(this@SplashActivity, LoginActivity::class.java)
                        } else {
                            Intent(this@SplashActivity, HomeActivity::class.java)
                        }
                        startActivity(
                            intent,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashActivity)
                                .toBundle()
                        )
                        finish()
                    }
                }
            }
        }
    }
}