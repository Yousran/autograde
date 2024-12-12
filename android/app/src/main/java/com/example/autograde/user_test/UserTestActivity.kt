package com.example.autograde.user_test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autograde.data.api.response.AnswersItem
import com.example.autograde.data.api.response.UpdateAnswer
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.databinding.ActivityUserTestBinding


class UserTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserTestBinding

    private val userTestViewModel: UserTestViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userTestId: String? = intent.getStringExtra("USER_TEST_ID")
        Log.d("CreatedTestActivity", "${userTestId}")

        if (userTestId != null) {
            userTestViewModel.getUserTestDetail(userTestId)

        } else {
            Log.e("CreatedTestActivity", "Test data is null")
            Toast.makeText(this, "Gagal memuat data tes", Toast.LENGTH_SHORT).show()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvAnswers.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvAnswers.addItemDecoration(itemDecoration)


        val adapter = UserTestAdapter(
            onGradeSelected = { answerId, grade ->
                val updateGrade = UpdateAnswer(
                    answerId = answerId,
                    grade = grade
                )
                userTestViewModel.updateGrade(updateGrade)
                userTestId?.let {
                    userTestViewModel.getUserTestDetail(userTestId)
                }
            },
            onItemClicked = { item ->
                // Klik item
            }
        )

        binding.rvAnswers.adapter = adapter

            observeUserTest(adapter)


        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeUserTest(adapter: UserTestAdapter) {
        userTestViewModel.updateResponse.observe(this) { message ->
            if (message.isNotBlank()) {
                Toast.makeText(this@UserTestActivity, message, Toast.LENGTH_SHORT).show()
            }
        }

        userTestViewModel.userTestResponse.observe(this) { user ->
            binding.tvUsername.text = user.username
            binding.tvResult.text = user.totalGrade.toString()
        }
        userTestViewModel.answerItemResponse.observe(this) { item ->
            adapter.submitList(item)
        }

        userTestViewModel.isLoading.observe (this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading : Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

