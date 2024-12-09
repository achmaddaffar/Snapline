package com.example.snapline.presentation.story_detail

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.snapline.data.remote.response.ListStoryItem
import com.example.snapline.databinding.ActivityStoryDetailBinding
import com.example.snapline.domain.model.StoryItem
import com.example.snapline.presentation.home.HomeActivity

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        val storyData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(HomeActivity.KEY_STORY_POST, StoryItem::class.java)
        } else {
            intent.getParcelableExtra(HomeActivity.KEY_STORY_POST)
        }

        with(binding) {
            Glide.with(this@StoryDetailActivity)
                .load(storyData?.photoUrl)
                .into(ivDetailPhoto)

            tvDetailName.text = storyData?.name
            tvDetailDescription.text = storyData?.description
        }
    }
}