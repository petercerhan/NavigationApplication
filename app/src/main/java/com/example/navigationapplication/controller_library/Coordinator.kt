package com.example.navigationapplication.controller_library

import com.example.navigationapplication.controller_library.container.ContainerViewModel
import com.example.navigationapplication.controller_library.container.Scene

interface Coordinator {
    val containerViewModel: ContainerViewModel
    val containerScene: Scene
}