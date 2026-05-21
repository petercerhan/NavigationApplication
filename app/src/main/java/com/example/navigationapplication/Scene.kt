package com.example.navigationapplication

import kotlin.reflect.KClass

class Scene(
    val viewModel: PlainViewModel,
    val fragmentType: KClass<out SceneFragment<*>>,
)