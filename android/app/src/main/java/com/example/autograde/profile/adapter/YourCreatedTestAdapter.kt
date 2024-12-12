package com.example.autograde.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autograde.data.api.response.TestsItem
import com.example.autograde.databinding.ItemListYourCreatedTestBinding

class YourCreatedTestAdapter  (private val onItemClicked: (TestsItem) -> Unit) :
    ListAdapter<TestsItem, YourCreatedTestAdapter.YourCreatdTestViewHolder>(DiffCallback()) {

    class YourCreatdTestViewHolder(private val binding: ItemListYourCreatedTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(test: TestsItem) {
           binding.tvTestTitle.text = test.testTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourCreatdTestViewHolder {
        val binding = ItemListYourCreatedTestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return YourCreatdTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: YourCreatdTestViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TestsItem>() {
        override fun areItemsTheSame(oldItem: TestsItem, newItem: TestsItem): Boolean {
            return oldItem.id == newItem.id // Ganti `id` sesuai field unik di model Anda
        }

        override fun areContentsTheSame(oldItem: TestsItem, newItem: TestsItem): Boolean {
            return oldItem == newItem
        }
    }
}