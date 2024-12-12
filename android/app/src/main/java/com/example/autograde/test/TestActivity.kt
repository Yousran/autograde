package com.example.autograde.test

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.autograde.R
import com.example.autograde.data.api.response.Answers
import com.example.autograde.data.api.response.ResultsItem
import com.example.autograde.databinding.ActivityTestBinding
import com.example.autograde.data.api.response.TestStart
import com.example.autograde.data.background_task.TestTimerWorker
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.data.local.entity.UserAnswer
import com.example.autograde.data.local.room.UserAnswerDao
import com.example.autograde.data.local.room.UserAnswerDatabase
import com.example.autograde.databinding.ActivityConfirmationBinding
import com.example.autograde.home.HomeActivity
import com.example.autograde.test_result.TestResultActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding
    private lateinit var testAdapter: TestAdapter
    private var currentQuestionIndex: Int = 0
    private lateinit var userAnswerDao: UserAnswerDao
    private lateinit var dialogBinding: ActivityConfirmationBinding
    private var customDialog: Dialog? = null
    private lateinit var timerFinishedReceiver: BroadcastReceiver

    private val _timeLeft = MutableLiveData<Int>()
    val timeLeft: LiveData<Int> get() = _timeLeft


    private val submitTestViewModel: SubmitTestViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private val testViewModel: TestViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = UserAnswerDatabase.getInstance(this)
        userAnswerDao = db.userAnswerDao()



        testAdapter = TestAdapter(
            { question, position ->
                saveCurrentAnswer()
                currentQuestionIndex = position
                displayQuestion(position)

                testAdapter.setCurrentlyDisplayedQuestion(position)
            },
            // Lambda untuk memeriksa apakah pertanyaan sudah dijawab
            { questionId ->
                testViewModel.isAnswered(questionId)
            },
            // Lambda untuk memeriksa apakah pertanyaan di-bookmark
            { questionId ->
                testViewModel.isQuestionBookmarked(questionId)
            },
            // Lambda untuk mendapatkan isi jawaban
            { questionId ->
                testViewModel.getAnswerByQuestionId(questionId)
            }
        )

        // Setup RecyclerView
        binding.rvNumberNav.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNumberNav.adapter = testAdapter


        val testData: TestStart? = intent.getParcelableExtra("TEST_START_OBJECT")
        val testId = intent.getStringExtra("USER_TEST_ID")

        if (testId != null) {
            updateTimer(testId) // Menampilkan data yang ada di database ke UI
        }

        CoroutineScope(Dispatchers.IO).launch {

            val isTimerRunning = userAnswerDao.isTimerRunning(testId ?: "")
            if (isTimerRunning == 0 && testData != null && testData.testDuration != null && testData.id != null) {
                val workRequest = OneTimeWorkRequestBuilder<TestTimerWorker>()
                    .setInputData(
                        workDataOf(
                            "testId" to testId,
                            "testDuration" to testData.testDuration / 60
                        )
                    )
                    .build()

                WorkManager.getInstance(applicationContext).enqueue(workRequest)
            }
        }



        testData?.let {
            binding.tvTestTitle.text = it.testTitle
            val nonNullQuestions =
                it.questions?.filterNotNull() ?: emptyList()
            testAdapter.submitList(nonNullQuestions)
            displayQuestion(0)

        }


        binding.btnPrevious.text = getString(R.string.previous)


        binding.btnNext.setOnClickListener {
            saveCurrentAnswer()
            if (currentQuestionIndex < testData?.questions?.size?.minus(1) ?: 0) {
                currentQuestionIndex++
                displayQuestion(currentQuestionIndex)
            } else {
                showCustomDialog(this)
            }
        }

        // Navigasi tombol previous
        binding.btnPrevious.setOnClickListener {
            saveCurrentAnswer()
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                displayQuestion(currentQuestionIndex)
            }
        }

        binding.btnBookmark.setOnClickListener {

            val testData: TestStart? = intent.getParcelableExtra("TEST_START_OBJECT")
            val currentQuestion = testData?.questions?.getOrNull(currentQuestionIndex)
            saveCurrentAnswer()

            currentQuestion?.let { question ->
                lifecycleScope.launch {
                    val isCurrentlyBookmarked =
                        userAnswerDao.isQuestionBookmarked(question.id ?: "")
                    val newBookmarkStatus = !isCurrentlyBookmarked
                    userAnswerDao.updateBookmarkStatus(question.id ?: "", newBookmarkStatus)

                    testAdapter.notifyItemChanged(currentQuestionIndex)
                    updateBookmarkedQuestion(newBookmarkStatus)
                    saveCurrentAnswer()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            showExitConfirmation(this@TestActivity)
        }


        timerFinishedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "com.example.autograde.TIMER_FINISHED") {
                    submitTest(0)
                    observeSubmitTestResponse()
                }
            }
        }

        val filter = IntentFilter("com.example.autograde.TIMER_FINISHED")
        registerReceiver(timerFinishedReceiver, filter)
    }


    private fun updateTimer(userTestId: String) {
        getRemainingTime(userTestId).observe(this) { remainingTime ->
            if (remainingTime != null) {
                val seconds = (remainingTime / 1000) % 60
                val minutes = (remainingTime / (1000 * 60)) % 60
                val hours = (remainingTime / (1000 * 60 * 60)) % 24

                val timeLeftInSecond = remainingTime
                _timeLeft.postValue(timeLeftInSecond.toInt())
                binding.tvTimer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                binding.tvTimer.text = "00:00"
            }
        }
    }


    private fun submitTest(timeLeft: Int) {
        val testId = intent.getStringExtra("USER_TEST_ID")
        val testData: TestStart? = intent.getParcelableExtra("TEST_START_OBJECT")

        if (testId != null && testData != null) {
            Log.d("TestActivity", "request called")
            lifecycleScope.launch {
                // Ambil semua jawaban yang tersimpan untuk `userTestId`
                val savedAnswers = userAnswerDao.getAllAnswersByUserTestId(testId)

                // Konversi jawaban ke dalam format `SubmitTestRequest`
                val answers = savedAnswers.map { savedAnswer ->
                    Answers(
                        questionId = savedAnswer.questionId,
                        answer = savedAnswer.answer ?: ""
                    )
                }
                submitTestViewModel.submitTest(testId, answers, timeLeft)
            }
        }
    }

    private fun displayQuestion(index: Int) {
        val testData: TestStart? = intent.getParcelableExtra("TEST_START_OBJECT")
        testData?.questions?.getOrNull(index)?.let { questions ->
            binding.tvQuestion.text = questions.questionText

            lifecycleScope.launch {
                val savedAnswer = userAnswerDao.getAnswerByQuestionId(questions.id ?: "")
                binding.edInputAnswer.setText(savedAnswer?.answer ?: "")
                val isBookmarked = userAnswerDao.isQuestionBookmarked(questions.id ?: "")
                updateBookmarkedQuestion(isBookmarked)
            }

            if (index == testData?.questions?.size?.minus(1)) {
                binding.btnNext.text =
                    getString(R.string.finish)  // Ganti tombol Next menjadi Finish
            } else {
                binding.btnNext.text =
                    getString(R.string.next)  // Kembali ke Next jika bukan soal terakhir
            }
        }
        testAdapter.notifyItemChanged(currentQuestionIndex)
        testAdapter.setCurrentlyDisplayedQuestion(index)
    }

    private fun saveCurrentAnswer() {
        val testData: TestStart? = intent.getParcelableExtra("TEST_START_OBJECT")
        val testResponse = intent.getStringExtra("USER_TEST_ID")
        val currentQuestion = testData?.questions?.getOrNull(currentQuestionIndex)
        testAdapter.notifyItemChanged(currentQuestionIndex)

        currentQuestion?.let { question ->
            val answerText = binding.edInputAnswer.text.toString()
                .trim() // Tambahkan trim() untuk menghapus spasi di awal/akhir
            if (testResponse != null) {
                lifecycleScope.launch {
                    val isBookmarked = userAnswerDao.isQuestionBookmarked(question.id ?: "")
                    val currentSequence = userAnswerDao.getMaxSequenceForUserTest(testResponse) ?: 0
                    val nextSequence = currentSequence + 1

                    // Simpan jawaban bahkan jika kosong
                    val userAnswer = UserAnswer(
                        userTestId = testResponse,
                        questionId = question.id ?: "",
                        answer = answerText,
                        isBookmarked = isBookmarked,
                        sequence = nextSequence
                    )

                    // Gunakan insertOrReplace atau update
                    val existingAnswer = userAnswerDao.getAnswerByQuestionId(question.id ?: "")
                    if (existingAnswer != null) {
                        userAnswerDao.updateAnswerByQuestionId(question.id ?: "", answerText)
                    } else {
                        userAnswerDao.insertAnswer(userAnswer)
                    }

                    testAdapter.notifyItemChanged(currentQuestionIndex)
                }
            }
        }
    }


    fun showCustomDialog(activity: AppCompatActivity) {
        Log.d("TestActivity", "showCustomDialog called")
        val dialog = Dialog(activity)
        dialogBinding = ActivityConfirmationBinding.inflate(layoutInflater)
        val view: View = dialogBinding.root
        dialog.setContentView(view)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvConfirmation.text = activity.getString(R.string.finish_test_confirmation)
        dialogBinding.btnContinue.text = activity.getString(R.string.finish)
        dialogBinding.btnBack.text = activity.getString(R.string.back)

        dialogBinding.btnContinue.setOnClickListener {
            lifecycleScope.launch {
                val remainingTime = timeLeft.value ?: 0
                val timeLeftInSecond = remainingTime / 1000
                submitTest(timeLeftInSecond)
            }
            observeSubmitTestResponse()
        }


        dialogBinding.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        this.customDialog = dialog // Menyimpan referensi dialog
        dialog.show()

        fun showDialogLoading(isLoading: Boolean) {
            dialogBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            dialogBinding.btnContinue.isEnabled = !isLoading
        }
        submitTestViewModel.isLoading.observe(this) {
            showDialogLoading(it)
        }
    }

    private fun observeSubmitTestResponse() {
        val testId = intent.getStringExtra("USER_TEST_ID")
        submitTestViewModel.resultItemResponse.observe(this) { result ->
            deleteTestSession(testId)
            deleteTestSession()
            if (result != null && result.isNotEmpty()) {
                val answers = ArrayList<ResultsItem>()
                answers.addAll(result)  // Memasukkan data ke answers

                submitTestViewModel.submitTestResponse.observe(this) { response ->
                    response.message?.let {
                        deleteTestSession(testId)
                        Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                    }

                    // Membuat Intent untuk mengirim data ke TestResultActivity
                    val intent = Intent(this@TestActivity, TestResultActivity::class.java).apply {
                        putExtra("TEST_RESPONSE_OBJECT", response) // Mengirimkan objek response
                        putParcelableArrayListExtra(
                            "RESULT_ITEM_OBJECT",
                            answers
                        ) // Mengirimkan answers yang berisi ArrayList<ResultsItem>
                    }

                    // Memulai aktivitas dan mengakhiri aktivitas ini
                    startActivity(intent)
                    finish()
                }
            } else {
                submitTestViewModel.errorMessage.observe(this) { error ->
                    Toast.makeText(this@TestActivity, error, Toast.LENGTH_SHORT).show()
                    deleteTestSession(testId)
                    deleteTestSession()
                }
            }

        }
    }


    private fun deleteTestSession(userTestId: String?) {
        userTestId?.let { userTestId ->
            lifecycleScope.launch {
                userAnswerDao.deleteAllAnswersByUserTestId(userTestId)
                binding.edInputAnswer.setText(null)
            }
        } ?: run {
            Toast.makeText(this, "User Test ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTestSession() {
        lifecycleScope.launch {
            userAnswerDao.deleteAllAnswers()
            binding.edInputAnswer.setText(null)
        }
    }


    private fun updateBookmarkedQuestion(isBookmark: Boolean) {
        if (isBookmark) {
            binding.btnBookmark.setImageResource(R.drawable.baseline_bookmark_24) // Gambar penuh
        } else {
            binding.btnBookmark.setImageResource(R.drawable.baseline_bookmark_border_24) // Gambar kosong
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
            intent = Intent(this@TestActivity, HomeActivity::class.java)
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

    fun getRemainingTime(userTestId: String): LiveData<Long> {
        return userAnswerDao.getRemainingTime(userTestId)
    }


    override fun onDestroy() {
        super.onDestroy()
        this.customDialog?.dismiss()
    }


}



