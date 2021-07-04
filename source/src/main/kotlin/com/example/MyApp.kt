package com.example

import com.example.view.MainView
import javafx.stage.Stage
import tornadofx.App
import com.example.painter.*
import com.example.graph.*
import tornadofx.add

class MyApp: App(MainView::class, Styles::class){
    override fun start(stage: Stage){
       // stage.isResizable = false
        stage.width = 1000.0
        stage.height = 1000.0
        super.start(stage)

    }
}