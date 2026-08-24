package com.example.navigationapplication.controller_library.container.animations

import android.content.Context
import android.view.animation.AnimationUtils
import com.example.navigationapplication.R
import kotlin.math.max

enum class BaseSceneTransitionAnimation(
    val enterAnimation: Int,
    val exitAnimation: Int,
) {
    SlideFromRight(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left),
    SlideFromLeft(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right),
    NoAnimation(0, 0);

    fun animationParameters(context: Context): Triple<Int, Int, Long> {
        val enterDuration = durationOf(context, enterAnimation)
        val exitDuration = durationOf(context, exitAnimation)
        return Triple(enterAnimation, exitAnimation, max(enterDuration, exitDuration))
    }

    private fun durationOf(context: Context, animationRes: Int): Long {
        if (animationRes == 0) {
            return 0L
        }
        return AnimationUtils.loadAnimation(context, animationRes).duration
    }
}
