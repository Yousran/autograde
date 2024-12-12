package com.example.autograde.create_test

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.autograde.R
import com.example.autograde.data.api.response.CreateQuestion
import com.example.autograde.data.api.response.CreateTestRequest
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.data.pref.UserModel
import com.example.autograde.databinding.ActivityConfirmationBinding
import com.example.autograde.databinding.ActivityCreateTestBinding
import com.example.autograde.home.HomeActivity
import com.example.autograde.login.LoginViewModel
import com.example.autograde.profile.ProfileActivity
import com.example.autograde.view_created_test.CreatedTestActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CreateTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTestBinding
    private lateinit var questionAdapter: ItemQuestionAdapter
    private lateinit var dialogBinding: ActivityConfirmationBinding
    private var customDialog: Dialog? = null

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private val createTestViewModel: CreateTestViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            showExitConfirmation(this@CreateTestActivity)
        }

        binding.btnBack.setOnClickListener {
            showExitConfirmation(this)
        }

        setupRecyclerView()

        setupAddQuestionButton()

        setupCreateTestButton()

        observeUserSession()

        binding.btnProfile.setOnClickListener {
            intent = Intent(this@CreateTestActivity, ProfileActivity::class.java)
            startActivity(intent)
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




    private fun setupRecyclerView() {
        questionAdapter = ItemQuestionAdapter()
        binding.rvQuestion.apply {
            layoutManager = LinearLayoutManager(this@CreateTestActivity)
            adapter = questionAdapter
        }

        // Tambah pertanyaan pertama secara default
        questionAdapter.addQuestion()

        // Listener untuk menghapus pertanyaan
        questionAdapter.onDeleteClickListener = { position ->
            // Cegah penghapusan jika hanya tersisa satu pertanyaan
            if (questionAdapter.itemCount > 1) {
                questionAdapter.removeQuestion(position)
            } else {
                Toast.makeText(this, "Minimal satu pertanyaan harus ada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAddQuestionButton() {
        binding.btnAdd.setOnClickListener {
            questionAdapter.addQuestion()
            binding.rvQuestion.scrollToPosition(questionAdapter.itemCount - 1)
        }
    }

    private fun setupCreateTestButton() {
        binding.btnCreateTest.setOnClickListener {
            // Validasi input
            if (validateInputs()) {
                showCustomDialog(this)
            }
        }
    }

    private fun validateInputs(): Boolean {
        // Validasi judul tes
        val testTitle = binding.edCreateTitleTest.text.toString().trim()
        if (testTitle.isEmpty()) {
            Toast.makeText(
                this@CreateTestActivity,
                "The test title cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Validasi durasi tes
        val testDuration = binding.edCreateTestDuration.text.toString().trim()
        if (testDuration.isEmpty()) {
            Toast.makeText(
                this@CreateTestActivity,
                "The duration cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Validasi pertanyaan
        val questions = questionAdapter.getAllQuestions()
        for ((index, question) in questions.withIndex()) {
            if (question.questionText.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    "Question ${index + 1} cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            if (question.answerText.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    "Answer key ${index + 1} cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }

        return true
    }

    private fun createTest() {
        val testTitle = binding.edCreateTitleTest.text.toString().trim()
        val testDuration = binding.edCreateTestDuration.text.toString().trim()
        val questions = questionAdapter.getAllQuestions()

        createTestViewModel.storeTest(
            CreateTestRequest(
                testTitle = testTitle,
                testDuration = testDuration.toInt() * 60,
                questions = questions.map {
                    CreateQuestion(
                        questionText = it.questionText,
                        answerText = it.answerText
                    )
                }
            )
        )
    }

    private fun TextInputEditText.setupErrorClear() {
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Hapus error saat input mendapat fokus
                (parent.parent as? TextInputLayout)?.error = null
            }
        }
    }

    fun showExitConfirmation(activity: AppCompatActivity) {
        val dialog = Dialog(activity)
        dialogBinding = ActivityConfirmationBinding.inflate(layoutInflater)
        val view: View = dialogBinding.root
        dialog.setContentView(view)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvConfirmation.text = activity.getString(R.string.exit_confirmation)
        dialogBinding.btnContinue.text = activity.getString(R.string.exit)
        dialogBinding.btnBack.text = activity.getString(R.string.cancel)

        dialogBinding.btnContinue.setOnClickListener {
            intent = Intent(this@CreateTestActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }

        dialogBinding.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        this.customDialog = dialog // Menyimpan referensi dialog
        dialog.show()
    }

    fun showCustomDialog(activity: AppCompatActivity) {
        val dialog = Dialog(activity)
        dialogBinding = ActivityConfirmationBinding.inflate(layoutInflater)
        val view: View = dialogBinding.root
        dialog.setContentView(view)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvConfirmation.text = activity.getString(R.string.create_test_confrimation)
        dialogBinding.btnContinue.text = activity.getString(R.string.create)
        dialogBinding.btnBack.text = activity.getString(R.string.back)

        dialogBinding.btnContinue.setOnClickListener {
            createTest()

            observeCreateTestResponse()

            createTestViewModel.isLoading.observe(this) {
                showDialogLoading(it)
            }
        }

        dialogBinding.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        this.customDialog = dialog
        dialog.show()
    }

    fun showDialogLoading(isLoading: Boolean) {
        dialogBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun observeCreateTestResponse() {
        createTestViewModel.createdTestResponse.observe(this) { body ->
            if (body != null && body.isNotEmpty()) {
                createTestViewModel.storeTestResponse.observe(this) { response ->
                    val intent = Intent(this@CreateTestActivity, CreatedTestActivity::class.java).apply {
                            putExtra("TEST_OBJECT", response)
                        }
                    startActivity(intent)
                    finish()
                }
            } else {
                createTestViewModel.errorMessage.observe(this) { response ->
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        customDialog?.dismiss()
    }
}
