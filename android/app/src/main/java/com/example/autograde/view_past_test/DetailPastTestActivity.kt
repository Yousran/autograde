package com.example.autograde.view_past_test

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.databinding.ActivityPastTestDetailBinding

class DetailPastTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPastTestDetailBinding

    private val pastTestViewModel: PastTestViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPastTestDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val testId: String? = intent.getStringExtra("TEST_ID")

        if (testId != null) {
            pastTestViewModel.showPastTestDetail(testId)

        } else {
            Log.e("CreatedTestActivity", "Test data is null")
            Toast.makeText(this, "Gagal memuat data tes", Toast.LENGTH_SHORT).show()
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvAnswers.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvAnswers.addItemDecoration(itemDecoration)


        val adapter = DetailPastTestAdapter()

        binding.rvAnswers.adapter = adapter

        observeUserTest(adapter)


        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeUserTest(adapter: DetailPastTestAdapter) {

        pastTestViewModel.pastTestDetailResponse.observe(this) { item ->
            binding.tvTestTitle.text = item.testTitle
            binding.tvResult.text = item.totalGrade.toString()
        }

        pastTestViewModel.resultItemPastResponse.observe(this) { item ->
            adapter.submitList(item)
        }

        pastTestViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}