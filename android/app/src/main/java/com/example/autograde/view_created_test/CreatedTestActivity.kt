package com.example.autograde.view_created_test


import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.autograde.R
import com.example.autograde.data.api.response.CreateTestResponse
import com.example.autograde.data.api.response.Test
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.data.pref.UserModel
import com.example.autograde.databinding.ActivityCreatedTestBinding
import com.example.autograde.login.LoginViewModel
import com.example.autograde.profile.ProfileActivity
import com.example.autograde.view_created_test.fragment.ItemQuestionFragment
import com.example.autograde.view_created_test.fragment.ItemResponseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreatedTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatedTestBinding

    private val createdTestViewModel: CreatedTestViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatedTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Observasi loading state
        createdTestViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnProfile.setOnClickListener {
            intent = Intent(this@CreatedTestActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Ambil test yang dibuat dari intent
        val testCreated: CreateTestResponse? = intent.getParcelableExtra("TEST_OBJECT")
        val testIdDetail : String? = intent.getStringExtra("TEST_ID")

        if (testCreated != null && testCreated.test?.id != null) {
            val testId = testCreated.test.id
            createdTestViewModel.getTestById(testId)
            Toast.makeText(this, "Test created", Toast.LENGTH_SHORT).show()
        }
        if (testIdDetail != null) {
            createdTestViewModel.getTestById(testIdDetail)
        } else {
            Log.e("CreatedTestActivity", "Test data is null")
        }

        observeTestResponse()
        observeAcceptResponse()
        observeUserSession()
        setupFragmentNavigation()
        observeItemQuestionById()

    }

    private fun observeTestDetails(test: Test) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                binding.tvTestTitle.text = test.testTitle
                if (test.testDuration != null) {
                    val testDuration = test.testDuration / 60
                    binding.tvTestMinutes.text =
                        getString(R.string.minutes, testDuration)
                }
                if (test?.acceptResponses != null) {
                    binding.switch1.isChecked = test.acceptResponses
                } else {
                    binding.switch1.isChecked = false
                }

                binding.switch1.setOnCheckedChangeListener { _, isChecked ->
                    // Log status sebelumnya dan status baru
                    Log.d(
                        "SwitchListener",
                        "Current state: ${test.acceptResponses}, New state: $isChecked"
                    )

                    // Pastikan bahwa statusnya berubah
                    if (isChecked != test.acceptResponses) {
                        // Jika ada perubahan, kirimkan request ke API
                        createdTestViewModel.acceptResponse(test.id, isChecked)
                    }
                }

                binding.btnShowQr.setOnClickListener {
                    intent = Intent(this@CreatedTestActivity, ShowCodeActivity::class.java).apply {
                        val testId = test.id
                        putExtra("TEST_ID", testId )
                    }
                    startActivity(intent)
                }
            }
        }
    }

    fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun observeAcceptResponse() {
        createdTestViewModel.acceptTestResponse.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this@CreatedTestActivity, response, Toast.LENGTH_SHORT).show()
            }
        }

        createdTestViewModel.errorMessage.observe(this) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(
                    this@CreatedTestActivity,
                    errorMsg,
                    Toast.LENGTH_SHORT
                ).show()

                // Kembalikan switch ke state sebelumnya jika gagal
                createdTestViewModel.testDetailResponse.value?.let { test ->
                    test.acceptResponses?.let { state ->
                        binding.switch1.isChecked = state
                    }
                }
            }
        }
    }

    private fun observeTestResponse() {
        createdTestViewModel.testDetailResponse.observe(this) { test ->
            if (test != null) {
                observeTestDetails(test)
            } else {
                createdTestViewModel.errorMessage.observe(this) { errorMsg ->
                    Toast.makeText(
                        this@CreatedTestActivity,
                        errorMsg ?: "Gagal memuat data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun observeUserSession() {
        loginViewModel.getSession().observe(this) { user ->
            updateUI(user)
        }
    }

    private fun updateUI(user: UserModel) {
        if (user.profilePictureUrl != null) {
            Glide.with(this)
                .load(user.profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.btnProfile)

        } else {
            Glide.with(this)
                .load(R.drawable.icon_profil)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.btnProfile)
        }
    }

    private fun setupFragmentNavigation() {
        val viewResponseFragment = ItemResponseFragment()
        val viewQuestionFragment = ItemQuestionFragment()
        updateButtonStyles(isResponseActive = true)


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_host, viewResponseFragment)
            .commit()


        binding.btnViewResponse.setOnClickListener {
            Log.d("FragmentNavigation", "View Response Button Clicked")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_host, viewResponseFragment)
                .commit()
            updateButtonStyles(isResponseActive = true)
        }

        binding.btnViewQuestion.setOnClickListener {
            Log.d("FragmentNavigation", "View Question Button Clicked")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_host, viewQuestionFragment)
                .commit()
            updateButtonStyles(isResponseActive = false)
        }
    }

    private fun updateButtonStyles(isResponseActive: Boolean) {
        if (isResponseActive) {
            binding.btnViewResponse.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.primary_fade)
            binding.btnViewResponse.setTextColor(ContextCompat.getColor(this, R.color.primary))
            binding.btnViewResponse.strokeColor =
                ContextCompat.getColorStateList(this, R.color.primary)

            binding.btnViewQuestion.backgroundTintList =
                ContextCompat.getColorStateList(this, android.R.color.transparent)
            binding.btnViewQuestion.setTextColor(ContextCompat.getColor(this, R.color.primary))
            binding.btnViewQuestion.strokeColor =
                ContextCompat.getColorStateList(this, R.color.primary)
        } else {
            binding.btnViewQuestion.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.primary_fade)
            binding.btnViewQuestion.setTextColor(ContextCompat.getColor(this, R.color.primary))
            binding.btnViewQuestion.strokeColor =
                ContextCompat.getColorStateList(this, R.color.primary)

            binding.btnViewResponse.backgroundTintList =
                ContextCompat.getColorStateList(this, android.R.color.transparent)
            binding.btnViewResponse.setTextColor(ContextCompat.getColor(this, R.color.primary))
            binding.btnViewResponse.strokeColor =
                ContextCompat.getColorStateList(this, R.color.primary)
        }
    }

    private fun observeItemQuestionById() {
        createdTestViewModel.testQuestionItemResponse.observe(this) { test ->
            Log.d("CreatedTestActivity", "Data diterima dari ViewModel: $test")
        }
    }

}
