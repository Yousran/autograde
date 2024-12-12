package com.example.autograde.profile

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.bumptech.glide.request.RequestOptions
import com.example.autograde.R
import com.example.autograde.data.api.response.Answer
import com.example.autograde.data.api.response.TestsItem
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.databinding.ActivityConfirmationBinding
import com.example.autograde.databinding.ActivityPastTestDetailBinding
import com.example.autograde.databinding.ProfileBinding
import com.example.autograde.login.LoginViewModel
import com.example.autograde.profile.adapter.YourCreatedTestAdapter
import com.example.autograde.profile.adapter.YourPastTestAdapter
import com.example.autograde.view_created_test.CreatedTestActivity
import com.example.autograde.view_past_test.DetailPastTestActivity


class ProfileActivity : AppCompatActivity() {

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    private lateinit var dialogBinding: ActivityConfirmationBinding

    private var customDialog: Dialog? = null

    private lateinit var binding: ProfileBinding

    private var isShowingAllTests = false
    private var isShowingPastTests = false

    private lateinit var yourCreatedTestAdapter: YourCreatedTestAdapter
    private lateinit var yourPastTestAdapter: YourPastTestAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeProfileResponse()
        observeYourCreatedTestResponse()
        observeYourPastTestResponse()

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation(this)
        }

        binding.tvViewAll1.setOnClickListener {
            isShowingAllTests = !isShowingAllTests
            // Perbarui dataset di adapter
            profileViewModel.allTestResponse.value?.let { test ->
                yourCreatedTestAdapter.submitList(if (isShowingAllTests) test else test.take(3))
            }
            updateViewAllText()
            updateUIForCreatedTest()

        }

        binding.tvViewAll2.setOnClickListener {
            isShowingPastTests = !isShowingPastTests
            // Perbarui dataset di adapter
            profileViewModel.pastTestResponse.value?.let { test ->
                yourPastTestAdapter.submitList(if (isShowingPastTests) test else test.take(3))
            }
            updateViewAllText()
            updateUIForPastTest()
        }

        profileViewModel.isLoading.observe(this) {
            showLoading(it)
        }

    }

    private fun updateUIForCreatedTest() {
        // Update UI berdasarkan status isShowingAllTests
        if (isShowingAllTests) {
            updateVisibilityForAllTests(isVisible = true)
            configureTextView(
                binding.tvYourCreatedTest,
                topMargin = 24,
                textSize = 24f,
                gravity = Gravity.CENTER_HORIZONTAL
            )
            binding.tvViewAll1.textSize = 20f
        } else {
            updateVisibilityForAllTests(isVisible = false)
            configureTextView(
                binding.tvYourCreatedTest,
                textSize = 17f
            )
            binding.tvViewAll1.textSize = 14f
        }
    }


    private fun updateUIForPastTest() {

        // Update UI berdasarkan status isShowingPastTests
        if (isShowingPastTests) {
            updateVisibilityForPastTests(isVisible = true)
            configureTextView(
                binding.tvYourPastTest,
                topMargin = 24,
                textSize = 24f,
                gravity = Gravity.CENTER_HORIZONTAL
            )
            binding.tvViewAll2.textSize = 20f
        } else {
            updateVisibilityForPastTests(isVisible = false)
            configureTextView(
                binding.tvYourPastTest,
                textSize = 17f
            )
            binding.tvViewAll2.textSize = 14f
        }
    }

    private fun updateVisibilityForAllTests(isVisible: Boolean) {
        binding.imgViewProfile.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.recyclerViewPastTests.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.btnLogout.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.tvUsername.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.tvEmail.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.tvYourPastTest.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.toolbar.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.tvViewAll2.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun updateVisibilityForPastTests(isVisible: Boolean) {
        binding.imgViewProfile.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.recyclerViewCreatedTests.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.btnLogout.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.tvUsername.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.tvEmail.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.tvYourCreatedTest.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.toolbar.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.tvViewAll1.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun configureTextView(
        textView: TextView,
        topMargin: Int? = null,
        textSize: Float? = null,
        gravity: Int? = null
    ) {
        topMargin?.let {
            val layoutParams = textView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = it
            textView.layoutParams = layoutParams
        }
        textSize?.let { textView.textSize = it }
        gravity?.let { textView.gravity = it }
    }


    private fun updateViewAllText() {
        binding.tvViewAll1.text = if (isShowingAllTests) {
            getString(R.string.back)
        } else {
            getString(R.string.view_all)
        }
        binding.tvViewAll2.text = if (isShowingPastTests) {
            getString(R.string.back)
        } else {
            getString(R.string.view_all)
        }

    }


    private fun observeProfileResponse() {
        profileViewModel.getProfile()
        profileViewModel.profileResponse.observe(this) { profile ->
            if (profile !== null) {
                binding.tvEmail.text = profile.email
                binding.tvUsername.text = profile.username

                if (profile.profilePictureUrl != null) {
                    Glide.with(this)
                        .load(profile.profilePictureUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imgViewProfile)
                } else {
                    Glide.with(this)
                        .load("https://picsum.photos/200")
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imgViewProfile)
                }

            } else {
                Toast.makeText(this@ProfileActivity, "terjadi kesalahan ", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    fun observeYourCreatedTestResponse() {
        profileViewModel.allTestResponse.observe(this) { test ->
            if (test !== null) {
                setListYourCreatedTest(test)
            } else {
                Toast.makeText(
                    this@ProfileActivity,
                    "Your created test is empty",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    fun observeYourPastTestResponse() {
        profileViewModel.pastTestResponse.observe(this) { test ->
            if (test != null) {
                setListYourPastTest(test)
            } else {
                Toast.makeText(this@ProfileActivity, "Your past test is empty", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    fun showLogoutConfirmation(activity: AppCompatActivity) {
        val dialog = Dialog(activity)
        dialogBinding = ActivityConfirmationBinding.inflate(layoutInflater)
        val view: View = dialogBinding.root
        dialog.setContentView(view)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvConfirmation.text = activity.getString(R.string.logout_confirmation)
        dialogBinding.btnContinue.text = activity.getString(R.string.logout)
        dialogBinding.btnBack.text = activity.getString(R.string.cancel)

        dialogBinding.btnContinue.setOnClickListener {
            loginViewModel.logout()
            finish()
        }

        dialogBinding.btnBack.setOnClickListener {
            dialog.dismiss()
        }

        this.customDialog = dialog // Menyimpan referensi dialog
        dialog.show()
    }

    private fun setListYourCreatedTest(test: List<TestsItem>) {
        if (!::yourCreatedTestAdapter.isInitialized) {
            yourCreatedTestAdapter = YourCreatedTestAdapter { item ->
                val intent = Intent(this, CreatedTestActivity::class.java)
                intent.putExtra("TEST_ID", item.id)
                startActivity(intent)
            }
            binding.recyclerViewCreatedTests.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewCreatedTests.adapter = yourCreatedTestAdapter
        }

        yourCreatedTestAdapter.submitList(if (isShowingAllTests) test else test.take(3))
        updateViewAllText()
    }

    private fun setListYourPastTest(test: List<TestsItem>) {
        if (!::yourPastTestAdapter.isInitialized) {
            yourPastTestAdapter = YourPastTestAdapter { item ->
                intent =
                    Intent(this@ProfileActivity, DetailPastTestActivity::class.java)
                intent.putExtra("TEST_ID", item.id)
                startActivity(intent)

            }
            binding.recyclerViewPastTests.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewPastTests.adapter = yourPastTestAdapter
        }

        yourPastTestAdapter.submitList(if (isShowingPastTests) test else test.take(3))
        updateViewAllText()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}