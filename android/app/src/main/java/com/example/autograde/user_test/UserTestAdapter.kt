package com.example.autograde.user_test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autograde.data.api.response.AnswersItem
import com.example.autograde.databinding.ItemListUserTestsBinding

class UserTestAdapter(
    private val onGradeSelected: (String, Int) -> Unit, // Callback untuk grade
    private val onItemClicked: (AnswersItem) -> Unit // Callback untuk klik item
) : ListAdapter<AnswersItem, UserTestAdapter.UserTestViewHolder>(DiffCallback()) {

    class UserTestViewHolder(private val binding: ItemListUserTestsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AnswersItem, onGradeSelected: (String, Int) -> Unit) {
            // Menampilkan teks jawaban dan pertanyaan
            binding.tvAnswer.text = item.answer
            binding.tvQuestion.text = item.question?.questionText

            // Grade dari item
            val grade = item.grade

            // Daftar TextViewCustom untuk setiap grade
            val grades = listOf(
                binding.tvGrade1, // Grade 1
                binding.tvGrade2, // Grade 2
                binding.tvGrade3, // Grade 3
                binding.tvGrade4, // Grade 4
                binding.tvGrade5  // Grade 5
            )

            // Iterasi TextViewCustom untuk mengatur isChecked dan listener
            grades.forEachIndexed { index, textViewCustom ->
                val currentGrade = index + 1

                // Atur isChecked berdasarkan nilai grade
                textViewCustom.isChecked = grade != null && grade == currentGrade

                // Listener untuk mengirim callback saat TextViewCustom diklik
                textViewCustom.setOnClickListener {
                    val answerId = item.id ?: return@setOnClickListener // Cek null pada id
                    onGradeSelected(answerId, currentGrade)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTestViewHolder {
        val binding = ItemListUserTestsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserTestViewHolder, position: Int) {
        val item = getItem(position)

        // Mengikat data ke ViewHolder
        holder.bind(item, onGradeSelected)

        // Listener untuk klik pada item (selain grade)
        holder.itemView.setOnClickListener {
            onItemClicked(item)
        }
    }

    // DiffUtil untuk mengoptimalkan perubahan dalam daftar
    class DiffCallback : DiffUtil.ItemCallback<AnswersItem>() {
        override fun areItemsTheSame(oldItem: AnswersItem, newItem: AnswersItem): Boolean {
            return oldItem.id == newItem.id // Gunakan ID unik
        }

        override fun areContentsTheSame(oldItem: AnswersItem, newItem: AnswersItem): Boolean {
            return oldItem == newItem // Bandingkan isi data
        }
    }

}
