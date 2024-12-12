package com.example.autograde.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.autograde.R
import com.example.autograde.databinding.ActivityMainBinding
import com.example.autograde.home.HomeForGuestActivity


class MainActivity : AppCompatActivity() {

    private val slideTransitionAnimation = SlideTransitionAnimation()
    private var currentStep = 1
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PopupAnimationManager().animatePopupEntry(
            titleView = binding.tvTitle,
            subtitleView = binding.tvSubtitle,
            imageView = binding.ivPlaceholder,
            informationView = binding.tvInformation,
            buttonView = binding.linearLayout
        )


        binding.btnSkip.setOnClickListener {
            navigateToHome()
        }


        binding.btnNext.setOnClickListener {
            if (currentStep == 1) {

                slideTransitionAnimation.animateSlideTransition(
                    oldTitleView = binding.tvInformation,
                    oldImageView = binding.ivPlaceholder,
                    newTitleText = getString(R.string.tv_information2),
                    newImageResourceId = R.drawable.splashscreen
                ) { newTitle, newImageId ->
                    binding.tvInformation.text = newTitle
                    binding.ivPlaceholder.setImageResource(newImageId)
                    binding.btnSkip.visibility = View.GONE
                    currentStep++
                }

            } else {
                navigateToHome()
            }
        }
    }


    private fun navigateToHome() {
        val intent = Intent(this, HomeForGuestActivity::class.java)
        startActivity(intent)
        finish()
    }
}