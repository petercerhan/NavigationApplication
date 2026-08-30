package com.example.navigationapplication.controller_library.container.animations

import com.example.navigationapplication.R

enum class ModalDismissalAnimation(
    val exitAnimation: Int,
    val duration: Long,
) {
    UncoverDown(R.anim.fragment_slide_out_bottom, 300L),
    FadeOut(R.anim.fragment_fade_out, 300L),
    NoAnimation(0, 0L)
}
