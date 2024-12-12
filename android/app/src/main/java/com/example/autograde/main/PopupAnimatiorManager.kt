package com.example.autograde.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator

class PopupAnimationManager {
    fun animatePopupEntry(
        titleView: View,
        subtitleView: View? = null,
        imageView: View? = null,
        informationView : View? = null,
        buttonView: View? = null
    ) {
        val offsetY = titleView.height.toFloat()

        // Set posisi awal view
        titleView.translationY = offsetY
        titleView.alpha = 0f

        val titleAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(titleView, View.TRANSLATION_Y, offsetY, 0f),
                ObjectAnimator.ofFloat(titleView, View.ALPHA, 0f, 1f)
            ).apply {
                duration = 200
                interpolator = DecelerateInterpolator()
            }
        }

        // List elemen untuk di-animate selanjutnya
        val elementsToAnimate = listOfNotNull(
            subtitleView,
            imageView,
            informationView,
            buttonView
        )

        // Animasi untuk elemen tambahan
        val secondaryAnimators = elementsToAnimate.mapIndexed { index, view ->
            AnimatorSet().apply {
                view.alpha = 0f
                view.scaleX = 0.8f
                view.scaleY = 0.8f

                playTogether(
                    ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f),
                    ObjectAnimator.ofFloat(view, View.SCALE_X, 0.8f, 1f),
                    ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.8f, 1f)
                ).apply {
                    startDelay = (index + 1) * 200L  // Delay antar elemen
                    duration = 100
                    interpolator = AccelerateDecelerateInterpolator()
                }
            }
        }

        // Jalankan animasi
        AnimatorSet().apply {
            playSequentially(
                titleAnimator,
                *secondaryAnimators.toTypedArray()
            )
            start()
        }
    }
}


class SlideTransitionAnimation {
    fun animateSlideTransition(
        oldTitleView: View,
        oldImageView: View,
        newTitleText: String,
        newImageResourceId: Int,
        onUpdateViews: (String, Int) -> Unit
    ) {
        // Animasi slide keluar ke kiri
        val slideOutX = ObjectAnimator.ofFloat(oldTitleView, View.TRANSLATION_X, 0f, -oldTitleView.width.toFloat())
        val slideOutImageX = ObjectAnimator.ofFloat(oldImageView, View.TRANSLATION_X, 0f, -oldImageView.width.toFloat())

        // Fade out
        val fadeOutTitle = ObjectAnimator.ofFloat(oldTitleView, View.ALPHA, 1f, 0f)
        val fadeOutImage = ObjectAnimator.ofFloat(oldImageView, View.ALPHA, 1f, 0f)

        // Animasi slide masuk dari kanan
        val slideInTitle = ObjectAnimator.ofFloat(oldTitleView, View.TRANSLATION_X, oldTitleView.width.toFloat(), 0f)
        val slideInImage = ObjectAnimator.ofFloat(oldImageView, View.TRANSLATION_X, oldImageView.width.toFloat(), 0f)

        // Fade in
        val fadeInTitle = ObjectAnimator.ofFloat(oldTitleView, View.ALPHA, 0f, 1f)
        val fadeInImage = ObjectAnimator.ofFloat(oldImageView, View.ALPHA, 0f, 1f)

        // Set animator
        AnimatorSet().apply {
            playTogether(
                slideOutX,
                slideOutImageX,
                fadeOutTitle,
                fadeOutImage
            )
            duration = 300
            interpolator = AccelerateInterpolator()

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Update konten
                    onUpdateViews(newTitleText, newImageResourceId)

                    // Set posisi awal sebelum animasi masuk
                    oldTitleView.translationX = oldTitleView.width.toFloat()
                    oldImageView.translationX = oldImageView.width.toFloat()

                    // Animasi masuk
                    AnimatorSet().apply {
                        playTogether(
                            slideInTitle,
                            slideInImage,
                            fadeInTitle,
                            fadeInImage
                        )
                        duration = 300
                        interpolator = DecelerateInterpolator()
                        start()
                    }
                }
            })
            start()
        }
    }
}