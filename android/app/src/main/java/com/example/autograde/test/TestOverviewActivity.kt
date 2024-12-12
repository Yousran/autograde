package com.example.autograde.test

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.autograde.R
import com.example.autograde.data.api.response.Test
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.data.pref.UserModel
import com.example.autograde.databinding.ActivityConfirmationBinding
import com.example.autograde.databinding.SecondTestOverviewBinding
import com.example.autograde.login.LoginViewModel
import com.example.autograde.profile.ProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TestOverviewActivity : AppCompatActivity() {

    private lateinit var binding: SecondTestOverviewBinding
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

        binding = SecondTestOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val testOverview: Test? = intent.getParcelableExtra("TEST_OBJECT")

        if (testOverview != null) {
            observeTestDetails(testOverview)
        }
        observeUserSession()

        binding.btnStart.setOnClickListener {
            showCustomDialog(this)
        }

        binding.actionProfil.setOnClickListener {
            intent = Intent(this@TestOverviewActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun observeTestDetails(testOverview: Test) {
        showLoading(true)
        if (testOverview.testDuration != null) {
            val testDuration = testOverview.testDuration
        }
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

    private fun observeUserSession() {
        loginViewModel.getSession().observe(this) { user ->
            // Perbarui UI dengan data user
            updateUI(user)
        }
    }

    private fun updateUI(user: UserModel) {
        binding.tvUsername.text = user.username
        if (user.profilePictureUrl != null) {
            Glide.with(this)
                .load(user.profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfileUsername)

            Glide.with(this)
                .load(user.profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.actionProfil)
        } else {
            Glide.with(this)
                .load(R.drawable.icon_profil)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivProfileUsername)

            Glide.with(this)
                .load(R.drawable.icon_profil)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.actionProfil)
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
            testViewModel.startTestById(testId)
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
                    val intent = Intent(this@TestOverviewActivity, TestActivity::class.java).apply {
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
                Toast.makeText(this, "Test created", Toast.LENGTH_SHORT).show()
            }

        }

    }

}