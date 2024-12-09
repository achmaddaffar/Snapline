package com.example.snapline.presentation.home

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapline.R
import com.example.snapline.databinding.ActivityHomeBinding
import com.example.snapline.presentation.maps.MapsActivity
import com.example.snapline.presentation.settings.SettingsActivity
import com.example.snapline.presentation.story_add.StoryAddActivity
import com.example.snapline.presentation.story_detail.StoryDetailActivity
import com.example.snapline.util.UiText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var dialog: Dialog

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.app_name)

        setupAction()
        collectState()
    }

    override fun onResume() {
        super.onResume()
        storyAdapter.refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                )
            }

            R.id.action_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAction() {
        with(binding) {
            dialog = Dialog(this@HomeActivity)
            dialog.setContentView(R.layout.dialog_loading)
            dialog.setCancelable(false)
            if (dialog.window != null)
                dialog.window?.setBackgroundDrawable(ColorDrawable(0))

            rvStoryList.layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
            storyAdapter = StoryAdapter { item ->
                val intent = Intent(this@HomeActivity, StoryDetailActivity::class.java)
                intent.putExtra(KEY_STORY_POST, item)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@HomeActivity)
                        .toBundle()
                )
            }
            rvStoryList.adapter = storyAdapter.withLoadStateHeaderAndFooter(
                header = LoadingStateAdapter {
                    storyAdapter.retry()
                },
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )

            fabPost.setOnClickListener {
                val intent = Intent(this@HomeActivity, StoryAddActivity::class.java)
                startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@HomeActivity)
                        .toBundle()
                )
            }
        }
    }

    private fun collectState() {
        viewModel.stories.observe(this@HomeActivity) { pagingData ->
            storyAdapter.submitData(lifecycle, pagingData.map { it.toStoryItem() })
        }
    }

    companion object {
        const val KEY_STORY_POST = "key_story_post"
    }
}