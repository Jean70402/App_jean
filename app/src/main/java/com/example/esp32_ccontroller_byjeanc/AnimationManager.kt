package com.example.esp32_ccontroller_byjeanc

import com.airbnb.lottie.LottieAnimationView

object AnimationManager {
    fun loadAnimation(animationView: LottieAnimationView, animationFileName: String) {
        val animationResId = animationView.resources.getIdentifier(animationFileName, "raw", animationView.context.packageName)
        animationView.setAnimation(animationResId)
        animationView.playAnimation()
    }
}