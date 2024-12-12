package com.example.autograde.view_created_test.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autograde.data.api.response.QuestionsShow
import com.example.autograde.databinding.ItemListQuestionCreatedTestBinding

class ItemQuestionAdapter (private val onItemClicked : (QuestionsShow) -> Unit ) : ListAdapter<QuestionsShow, ItemQuestionAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuestionsShow>() {
            override fun areItemsTheSame(
                oldItem: QuestionsShow,
                newItem: QuestionsShow
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: QuestionsShow,
                newItem: QuestionsShow
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class MyViewHolder (val binding : ItemListQuestionCreatedTestBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (question : QuestionsShow){
            binding.tvQuestion.text = question.questionText
            binding.tvAnswer.text = question.answerText
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemListQuestionCreatedTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val question = getItem(position)
        holder.bind(question)
        holder.itemView.setOnClickListener{
            onItemClicked(question)
        }

    }

}