package com.example.autograde.register

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.autograde.data.api.response.RegisterRequest
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.databinding.ActivityRegisterBinding
import com.example.autograde.login.LoginActivity
import com.example.autograde.data.pref.UserPreference
import com.example.autograde.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        binding.registerButton.setOnClickListener {
            binding.registerButton.isEnabled = false
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edRegisterConfirmPassword.text.toString()

            when {
                name.isBlank() -> {
                    binding.registerButton.isEnabled = true
                    Toast.makeText(this, "Name field must be filled in", Toast.LENGTH_SHORT).show()
                }
                email.isBlank() -> {
                    binding.registerButton.isEnabled = true
                    Toast.makeText(this, "Email field must be filled in", Toast.LENGTH_SHORT).show()
                }
                password.isBlank() -> {
                    binding.registerButton.isEnabled = true
                    Toast.makeText(this, "Password field must be filled in", Toast.LENGTH_SHORT).show()
                }
                confirmPassword.isBlank() -> {
                    binding.registerButton.isEnabled = true
                    Toast.makeText(this, "Confirm Password field must be filled in", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    binding.registerButton.isEnabled = true
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val registerRequest = RegisterRequest(
                        email = email,
                        password = password,
                        username = name
                    )
                    registerViewModel.registerUser(registerRequest)
                }
            }
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        observeViewModel()
        binding.tvLoginHere.setOnClickListener {
            intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        registerViewModel.registerResponse.observe(this) { response ->
            binding.registerButton.isEnabled = true
            if (response.user !== null && response.message !== null) {
                showSuccess(response.message)
            } else {
                showMessage(response?.message)
            }
        }
    }

    private fun showMessage(message: String?) {
        AlertDialog.Builder(this)
            .setMessage(message ?: "Terjadi kesalahan")
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun showSuccess(message: String?) {
        Toast.makeText(this, "Registrasi berhasil: ${message ?: ""}", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(1000).apply {
            startDelay = 100
            start()
        }
    }

    private fun showLoading(isLoading : Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}