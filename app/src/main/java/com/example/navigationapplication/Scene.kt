package com.example.navigationapplication

import kotlin.reflect.KClass

class Scene(
    val viewModel: ApplicationViewModel,
    val fragmentType: KClass<out SceneFragment<*>>,
)