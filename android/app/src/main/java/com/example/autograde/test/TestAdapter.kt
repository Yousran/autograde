package com.example.autograde.test

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.autograde.R
import com.example.autograde.data.api.response.StartQuestionsItem
import com.example.autograde.databinding.ItemNavNumberBinding

class TestAdapter(
    private val onQuestionClick: (StartQuestionsItem, Int) -> Unit,
    private val isAnsweredCheck: (String) -> LiveData<Boolean>, // Mengubah menjadi LiveData
    private val isBookmarkedCheck: (String) -> LiveData<Boolean>, // Mengubah menjadi LiveData
    private val getAnswerCheck: (String) -> LiveData<String?> // Mengubah menjadi LiveData
) : RecyclerView.Adapter<TestAdapter.ViewHolder>() {

    private val questions = mutableListOf<StartQuestionsItem>()
    private var currentlyDisplayedQuestionIndex: Int = -1

    fun submitList(newQuestions: List<StartQuestionsItem>?) {
        questions.clear()
        newQuestions?.let { questions.addAll(it) }
        notifyDataSetChanged()
    }

    fun setCurrentlyDisplayedQuestion(position: Int) {
        val previousIndex = currentlyDisplayedQuestionIndex
        currentlyDisplayedQuestionIndex = position

        if (previousIndex != -1) {
            notifyItemChanged(previousIndex)
        }
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNavNumberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]

        // Mengamati jawaban untuk pertanyaan ini
        getAnswerCheck(question.id ?: "").observe(holder.itemView.context as LifecycleOwner) { answer ->
            val isAnswered = !answer.isNullOrBlank()

            // Mengamati bookmark status
            isBookmarkedCheck(question.id ?: "").observe(holder.itemView.context as LifecycleOwner) { isBookmarked ->
                // Mengamati status jawaban
                isAnsweredCheck(question.id ?: "").observe(holder.itemView.context as LifecycleOwner) { isAnsweredStatus ->
                    holder.bind(
                        question,
                        position,
                        isAnsweredStatus,
                        isBookmarked,
                        position == currentlyDisplayedQuestionIndex
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int = questions.size

    inner class ViewHolder(private val binding: ItemNavNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            question: StartQuestionsItem,
            position: Int,
            isAnswered: Boolean,
            isBookmarked: Boolean,
            isCurrentlyDisplayed: Boolean
        ) {
            val textColor = when {
                isBookmarked && isCurrentlyDisplayed -> R.color.primary
                isCurrentlyDisplayed -> R.color.white
                isBookmarked && isAnswered -> R.color.primary
                isBookmarked -> R.color.primary
                isAnswered -> R.color.primary
                else -> R.color.primary
            }

            val backgroundColor = when {
                isBookmarked && isCurrentlyDisplayed -> R.drawable.bg_nav_number_warning
                isCurrentlyDisplayed -> R.drawable.bg_nav_number_active
                isBookmarked && isAnswered -> R.drawable.bg_nav_number_warning
                isBookmarked -> R.drawable.bg_nav_number_warning
                isAnswered -> R.drawable.bg_nav_number_filled
                else -> R.drawable.bg_nav_number_inactive
            }

            binding.tvNumber.text = (position + 1).toString()
            binding.tvNumber.setTextColor(ContextCompat.getColor(binding.root.context, textColor))
            binding.root.setBackgroundResource(backgroundColor)

            binding.root.setOnClickListener {
                if (currentlyDisplayedQuestionIndex != position) {
                    onQuestionClick(question, position)
                }
            }
        }
    }
}
