package com.example.navigationapplication.controller_library.container.animations

import com.example.navigationapplication.R

enum class ReplaceModalAnimation(
    val enterAnimation: Int,
    val exitAnimation: Int,
    val duration: Long,
) {
    Fade(R.anim.fragment_fade_in, R.anim.fragment_fade_out, 300L),
    SlideFromRight(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left, 300L),
    SlideFromLeft(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right, 300L),
    NoAnimation(0, 0, 0L);
}
