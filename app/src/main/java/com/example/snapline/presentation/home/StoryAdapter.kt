package com.example.snapline.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapline.databinding.ItemStoryBinding
import com.example.snapline.domain.model.StoryItem

class StoryAdapter(
    private val onItemClick: (StoryItem) -> Unit
): PagingDataAdapter<StoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryItem) {
            with(binding) {
                tvItemName.text = item.name
                tvItemDescription.text = item.description

                Glide.with(binding.root)
                    .load(item.photoUrl)
                    .into(ivItemPhoto)

                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) holder.bind(data)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}