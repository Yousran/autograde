package com.example.autograde.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.autograde.data.di.ViewModelFactory
import com.example.autograde.databinding.ActivityLoginBinding
import com.example.autograde.home.HomeActivity
import com.example.autograde.register.RegisterActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        loginViewModel.loginResponse.observe(this, { response ->
            showSuccess(response.message)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        })

        loginViewModel.isLoading.observe (this) {
            showLoading(it)
        }

        loginViewModel.errorMessage.observe(this, { error ->
           showMessage(error)
        })

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (email.isEmpty() && password.isEmpty()) {
                binding.emailEditText.error = "Email dan password tidak boleh kosong"
            } else {
                loginViewModel.loginUser(email, password)
            }
        }

        binding.tvRegisterHere.setOnClickListener {
            intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
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

    private fun showMessage(message: String?) {
        AlertDialog.Builder(this)
            .setMessage(message ?: "Terjadi kesalahan")
            .setPositiveButton("OK") { _, _ -> }
            .show()
    }

    private fun showSuccess(message: String?) {
        Toast.makeText(this, "${message ?: ""}", Toast.LENGTH_SHORT).show()
    }

}