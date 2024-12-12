package com.example.autograde.create_test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.autograde.data.api.response.CreateQuestion
import com.example.autograde.databinding.ActivityItemQuestionInputBinding

class ItemQuestionAdapter : RecyclerView.Adapter<ItemQuestionAdapter.QuestionViewHolder>() {

    private val questions = mutableListOf(CreateQuestion())

    var onDeleteClickListener: ((Int) -> Unit)? = null
    var onQuestionChangedListener: ((Int, CreateQuestion) -> Unit)? = null

    inner class QuestionViewHolder(private val binding: ActivityItemQuestionInputBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: CreateQuestion, position: Int) {


            binding.tvQuestionNumber.text = "${position + 1}"

            binding.edInputQuestion.setText(question.questionText ?: "")
            binding.edInputAnswerKey.setText(question.answerText ?: "")

            // Listener untuk perubahan pertanyaan
            binding.edInputQuestion.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    question.questionText = binding.edInputQuestion.text.toString()
                    onQuestionChangedListener?.invoke(position, question)
                }
            }

            // Listener untuk perubahan kunci jawaban
            binding.edInputAnswerKey.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    question.answerText = binding.edInputAnswerKey.text.toString()
                    onQuestionChangedListener?.invoke(position, question)
                }
            }

            // Listener tombol hapus
            binding.btnDelete.setOnClickListener {
                onDeleteClickListener?.invoke(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ActivityItemQuestionInputBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], position)
    }

    override fun getItemCount(): Int = questions.size

    // Fungsi untuk menambah pertanyaan
    fun addQuestion() {
        questions.add(CreateQuestion())
        notifyItemInserted(questions.size - 1)
    }

    // Fungsi untuk menghapus pertanyaan
    fun removeQuestion(position: Int) {
        if (position in questions.indices) {
            questions.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, questions.size)
        }
    }

    // Fungsi untuk mendapatkan semua pertanyaan
    fun getAllQuestions(): List<CreateQuestion> = questions

    // Data class untuk menyimpan pertanyaan
}