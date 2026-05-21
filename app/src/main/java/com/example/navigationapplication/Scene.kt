package com.example.navigationapplication

import kotlin.reflect.KClass

class Scene(
    val viewModelId: String,
    val fragmentType: KClass<out SceneFragment<*>>,
)