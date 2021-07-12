package com.example.visualised

import com.example.graph.Vertex
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextInputDialog
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.Path
import javafx.scene.shape.QuadCurve
import javafx.scene.text.Font
import tornadofx.getChildList
import tornadofx.removeFromParent
import com.example.graphcontroller.*
import javafx.scene.effect.Light
import javafx.scene.layout.Pane
import tornadofx.add
import tornadofx.isInt

class VisualisedVertex(
    var gc: GraphController,
    var vertex: Vertex,
    var xy: Pair<Double, Double>,
    var highlightColor: Color = Color.TRANSPARENT,
    var highlighted: Boolean = false,
    var nodesList: MutableList<Node>){

    init{
        for (a in nodesList){
            a.setOnMouseClicked{
                when(gc.state){

                    GraphControllerState.DELETINGVERTEX ->{
                        gc.delVisualVertex(this)
                        if (gc.graph!!.getVertices().size == 0)
                            gc.graphIsSet = false
                        gc.state = GraphControllerState.NOTEDITING
                    }

                    GraphControllerState.CHOOSINGFIRSTVERTEX -> {
                        gc.highlightVertices(listOf(this), Color.GREEN)
                        gc.tmpVertexPair = Pair(this.vertex, null)
                        gc.state = GraphControllerState.CHOOSINGSECONDVERTEX
                    }

                    GraphControllerState.CHOOSINGSECONDVERTEX ->{
                        gc.highlightVertices(listOf(this), Color.GREEN)
                        gc.tmpVertexPair = Pair(gc.tmpVertexPair.first!!, this.vertex)
                        ///adding from dialog text input
                        var dialog = TextInputDialog()
                        dialog.headerText = ""
                        dialog.contentText = "Введите вес ребра"
                        val w = dialog.showAndWait()
                        if (w.isPresent)
                            if (w.get().isInt() && w.get().toInt() > 0)
                                gc.addVisualEdge(gc.tmpVertexPair.first!!, gc.tmpVertexPair.second!!, w.get().toInt())
                            else{
                                showErrorAlert("Введен некорректный вес")
                            }
                        gc.updateVisualEdges()
                        gc.state = GraphControllerState.NOTEDITING
                    }
                    else -> {}
                }
            }

            a.setOnMouseDragged{
                if (gc.state == GraphControllerState.NOTEDITING) {
                    gc.highlightVertices(listOf(this), Color.BLUE)
                    val xcntr = it.x + this.nodesList.first().layoutX -25
                    val ycntr = it.y + this.nodesList.first().layoutY - 25
                    if (gc.isInsidePane(xcntr, ycntr)) {
                        gc.changeLayout(this, xcntr , ycntr)
                    }
                }
            }
            a.setOnMouseReleased{
                if (gc.state == GraphControllerState.NOTEDITING){
                    gc.updateVisualEdges()
                }
            }
            a.setOnMouseEntered{

                when (gc.state){
                    GraphControllerState.DELETINGVERTEX -> {
                        gc.highlightVertices(listOf(this), Color.DARKRED)
                    }
                }
                it.consume()
            }
            a.setOnMouseExited{
                if (gc.state != GraphControllerState.CHOOSINGSECONDVERTEX && gc.state != GraphControllerState.RUNNING_ALGORITHM && highlighted == false)
                    gc.highlightVertices(listOf(this), Color.BLACK, 2.0)
            }
        }
    }
}
class VisualisedEdge(
    var gc: GraphController,
    var edge: Triple<Vertex, Vertex, Int>,
    var startX: Double,
    var startY: Double,
    var endX: Double,
    var endY: Double,
    var highlightColor: Color = Color.TRANSPARENT,
    var highlighted: Boolean = false,
    var nodesList: MutableList<Node>){

    init{
        for (a in nodesList){
            a.setOnMouseClicked{
                when(gc.state){
                    GraphControllerState.DELETINGEDGE ->{
                        a.setStyle("-fx-background-color: red;")
                        a.removeFromParent()
                        gc.graph!!.delEdge(edge.first, edge.second)
                        gc.state = GraphControllerState.NOTEDITING
                        gc.updateVisualEdges()
                    }
                    else -> {}
                }
            }
            a.setOnMouseEntered{
                if (gc.state == GraphControllerState.NOTEDITING)
                    gc.hightLightWithOpacity(this)

                when(gc.state){
                    GraphControllerState.NOTEDITING -> {
                        if (!highlighted) {
                            gc.highlightEdges(
                                listOf(this),
                                width = 5.0,
                                color = Color.rgb(103, 99, 98),
                                type = "bold",
                                labelColor = Color.BLACK,
                                fontSize = 25.0
                            )

                        }
                    }
                    GraphControllerState.DELETINGEDGE -> {
                        gc.highlightEdges(listOf(this), width = 5.0, type = "bold", color = Color.RED, labelColor = Color.DARKRED, fontSize = 25.0)
                    }
                }
            }
            //restore original styles
            a.setOnMouseExited{

                when(gc.state){
                    GraphControllerState.RUNNING_ALGORITHM -> {}
                    else -> {
                        if (!highlighted){
                            gc.highlightEdges(listOf(this), width = 2.0, type = "normal", color = Color.BLACK, labelColor = Color.BLACK, fontSize = 17.0)
                            gc.restoreOpacities()
                        }
                    }
                }
            }
        }
    }

}