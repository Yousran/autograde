package com.example.autograde.test

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.autograde.R
import com.example.autograde.data.api.response.Test
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.databinding.ActivityConfirmationBinding
import com.example.autograde.databinding.FirstTestOverviewBinding
import com.example.autograde.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TestOverviewForGuestActivity : AppCompatActivity() {

    private lateinit var binding: FirstTestOverviewBinding
    private lateinit var dialogBinding: ActivityConfirmationBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private val testViewModel: TestViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private var customDialog: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FirstTestOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val testOverview: Test? = intent.getParcelableExtra("TEST_OBJECT")

        if (testOverview != null) {
            observeTestDetails(testOverview)
        }

        binding.btnStart.setOnClickListener {
            showCustomDialog(this)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun observeTestDetails(testOverview: Test) {
        showLoading(true)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                delay(500)
            }
            withContext(Dispatchers.Main) {
                binding.tvTestTitle.text = testOverview.testTitle
                if (testOverview.testDuration != null) {
                    val testDuration = testOverview.testDuration / 60
                    binding.textMinutes.text =
                        getString(R.string.minutes, testDuration)
                }
                binding.textNumber.text =
                    getString(R.string.number, testOverview.questionCount ?: 0)
                binding.testUsers.text = getString(R.string.users, testOverview.userTestCount ?: 0)
                showLoading(false)
            }
        }
    }


    fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun showDialogLoading(isLoading: Boolean) {
        dialogBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun showCustomDialog(activity: AppCompatActivity) {
        val dialog = Dialog(activity)
        dialogBinding = ActivityConfirmationBinding.inflate(layoutInflater)
        val view: View = dialogBinding.root
        dialog.setContentView(view)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvConfirmation.text = activity.getString(R.string.start_test_confirmation)
        dialogBinding.btnContinue.text = activity.getString(R.string.start)
        dialogBinding.btnBack.text = activity.getString(R.string.back)

        dialogBinding.btnContinue.setOnClickListener {
            val testOverview: Test? = intent.getParcelableExtra("TEST_OBJECT")
            val testId = testOverview?.id.toString()
            val username = binding.edUsername.text.toString()
            testViewModel.startTestForGuest(testId, username)
            observeTestResponse()
            observeStartTestResponse()

            testViewModel.isLoading.observe(this) {
                showDialogLoading(it)
            }
        }

        dialogBinding.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        this.customDialog = dialog // Menyimpan referensi dialog
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        customDialog?.dismiss()
    }


    private fun observeTestResponse() {
        testViewModel.testResponse.observe(this) { testList ->
            if (testList != null && testList.isNotEmpty()) {
                val test = testList[0]
                testViewModel.startTestResponse.observe(this) { response ->
                    val userTestId = response.userTestId
                    val intent =
                        Intent(this@TestOverviewForGuestActivity, TestActivity::class.java).apply {
                            putExtra(
                                "TEST_START_OBJECT",
                                test
                            )  // Pass the test object to the next activity
                            putExtra("USER_TEST_ID", userTestId)
                        }
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Kode Test Tidak ditemukkan", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun observeStartTestResponse() {
        testViewModel.startTestResponse.observe(this) { response ->
            response.message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }

        }

    }
}
