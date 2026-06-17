package com.example.navigationapplication.root_container

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.controller_library.SceneFragment
import kotlin.reflect.KClass

class Scene(
    val viewModel: ApplicationViewModel,
    val fragmentType: KClass<out SceneFragment<*>>,
)