package com.example.snapline.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.snapline.databinding.ItemLoadingBinding

class LoadingStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadingStateAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            with(binding) {
                if (loadState is LoadState.Error)
                    errorMsg.text = loadState.error.localizedMessage
                progressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
                errorMsg.isVisible = loadState is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        return ViewHolder(ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}