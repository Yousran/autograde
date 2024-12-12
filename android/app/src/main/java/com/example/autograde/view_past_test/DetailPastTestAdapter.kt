package com.example.autograde.view_past_test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autograde.data.api.response.ResultsItemPast
import com.example.autograde.databinding.ItemListUserTestsBinding


class DetailPastTestAdapter () : ListAdapter<ResultsItemPast, DetailPastTestAdapter.PastTestViewHolder>(DiffCallback()) {

    class PastTestViewHolder(private val binding: ItemListUserTestsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ResultsItemPast) {
            binding.tvAnswer.text = item.answer
            binding.tvQuestion.text = item.question

            val grade = item.grade

            val grades = listOf(
                binding.tvGrade1,
                binding.tvGrade2,
                binding.tvGrade3,
                binding.tvGrade4,
                binding.tvGrade5
            )

            grades.forEachIndexed { index, textViewCustom ->
                val currentGrade = index + 1

                textViewCustom.isChecked = grade != null && grade == currentGrade
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastTestViewHolder {
        val binding = ItemListUserTestsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PastTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PastTestViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DiffCallback : DiffUtil.ItemCallback<ResultsItemPast>() {
        override fun areItemsTheSame(oldItem: ResultsItemPast, newItem: ResultsItemPast): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ResultsItemPast, newItem: ResultsItemPast): Boolean {
            return oldItem == newItem
        }
    }

}
