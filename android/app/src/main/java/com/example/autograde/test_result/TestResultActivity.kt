package com.example.autograde.test_result

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autograde.data.api.response.Answer
import com.example.autograde.data.api.response.SubmitTestResponse
import com.example.autograde.databinding.ActivityTestResultBinding
import com.example.autograde.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestResultBinding
    private lateinit var adapter: TestResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Home button action
        binding.btnHome.setOnClickListener {
            val intent = Intent(this@TestResultActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Retrieve data from Intent
        val resultTest: SubmitTestResponse? = intent.getParcelableExtra("TEST_RESPONSE_OBJECT")

        // Observe test result
        observeTestResult(resultTest)

    }

    private fun observeTestResult(testResult: SubmitTestResponse? ) {
        showLoading(true)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                delay(500) // Simulate loading delay
            }
            withContext(Dispatchers.Main) {
                binding.tvTestTitle.text = testResult?.testTitle
                binding.tvGrade.text = testResult?.totalGrade.toString()
                showLoading(false)
            }
            testResult?.results?.let { results ->
                val answers = results.mapNotNull { it?.answer }
                setRecyclerView(answers)
            }

            showLoading(false)
        }

    }

    private fun setRecyclerView(answer: List<Answer>) {
        val adapter = TestResultAdapter {

        }
        adapter.submitList(answer)
        binding.rvResult.layoutManager = LinearLayoutManager(this)
        binding.rvResult.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
