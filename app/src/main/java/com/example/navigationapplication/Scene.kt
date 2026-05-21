package com.example.navigationapplication

import kotlin.reflect.KClass

class Scene(
    val viewModelId: Int,
    val fragmentType: KClass<out SceneFragment<*>>,
)