package com.example.navigationapplication.controller_library.container.animations

import android.content.Context
import android.view.animation.AnimationUtils
import com.example.navigationapplication.R
import kotlin.math.max

enum class BaseSceneTransitionAnimation(
    val enterAnimation: Int,
    val exitAnimation: Int,
    val duration: Long,
) {
    SlideFromRight(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left, 300L),
    SlideFromLeft(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right, 300L),
    NoAnimation(0, 0, 0L);
}
