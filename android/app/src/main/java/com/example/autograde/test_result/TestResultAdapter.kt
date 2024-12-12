package com.example.autograde.test_result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autograde.data.api.response.Answer
import com.example.autograde.databinding.ItemListTestResultBinding

class TestResultAdapter(private val onItemClicked: (Answer) -> Unit) :
    ListAdapter<Answer, TestResultAdapter.TestResultViewHolder>(DiffCallback()) {

    class TestResultViewHolder(private val binding: ItemListTestResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answer) {
            binding.tvAnswer.text = answer.answer
            binding.tvScore.text = answer.grade.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestResultViewHolder {
        val binding = ItemListTestResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TestResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestResultViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Answer>() {
        override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.id == newItem.id // Ganti `id` sesuai field unik di model Anda
        }

        override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem == newItem
        }
    }
}
