package com.example.view

import com.example.Styles
import com.example.graph.*
import tornadofx.*
import com.example.painter.*
import javafx.scene.control.Label
import javafx.scene.shape.Rectangle

class MainView : View() {
    override val root = hbox {
        var g = Graph(graphType = GraphType.RandomGraph)
        var p = Painter()
        add(p.paintGraph(g))
    }
}
