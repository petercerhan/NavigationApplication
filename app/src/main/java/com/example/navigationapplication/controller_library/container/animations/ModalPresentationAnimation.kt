package com.example.navigationapplication.controller_library.container.animations

import com.example.navigationapplication.R

enum class ModalPresentationAnimation(
    val enterAnimation: Int,
    val duration: Long,
) {
    CoverFromBottom(R.anim.fragment_slide_in_bottom, 300L),
    FadeIn(R.anim.fragment_fade_in, 300L),
    NoAnimation(0, 0L)
}
