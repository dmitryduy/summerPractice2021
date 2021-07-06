package com.example.view

import com.example.graph.*
import tornadofx.*
import com.example.painter.*
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import com.example.graphcontroller.*

class Row(val array: Array<Any>)

private val columns = listOf(
    Row(arrayOf("∞", "∞", "∞", 0, "∞", "∞", "∞", "∞", "∞", "∞", "∞", "∞")),
    Row(arrayOf(2, "∞", "∞", "", 9, "∞", 2, "∞", "∞", "∞", "∞", "∞")),
    Row(arrayOf("", 5, "∞", "", 6, "∞", 2, "∞", "∞", "∞", "∞", "∞")),
    Row(arrayOf("", 5, "∞", "", 6, "∞", "", 12, "∞", "∞", 8, "∞")),
    Row(arrayOf("", "", 10, "", 6, "∞", "", 12, "∞", "∞", 8, "∞")),
    Row(arrayOf("", "", 7, "", "", 9, "", 12, "∞", "∞", 8, "∞")),
    Row(arrayOf("", "", "", "", "", 9, "", 12, "∞", "∞", 8, "∞")),
    Row(arrayOf("", "", "", "", "", 9, "", 12, 9, 14, "", 11)),
    Row(arrayOf("", "", "", "", "", "", "", 12, 9, 14, "", 11)),
    Row(arrayOf("", "", "", "", "", "", "", 12, "", 14, "", 11)),
    Row(arrayOf("", "", "", "", "", "", "", 12, "", 14, "", "")),
    Row(arrayOf("", "", "", "", "", "", "", "", "", 14, "", "")),
).asObservable()

val vertexes = observableListOf<String>("A", "B", "C", "D", "E", "F", "J","H", "I", "J", "K", "L")

val foundPaths = observableListOf<String>("A->B: AB", "A->C: ABC", "A->E: ABCDE","A->B: AB", "A->C: ABC", "A->E: ABCDE","A->B: AB", "A->C: ABC", "A->E: ABCDE","A->B: AB", "A->C: ABC", "A->E: ABCDE")
val currentAction = "Какое-то действие, которое проиходит прямо сейчас"
val order = observableListOf<String>("A", "B", "C", "D", "E")


class MainView : View("Алгоритм Дейкстры") {
    override val root : AnchorPane by fxml("layout.fxml")
    private val vbox: VBox by fxid("foundPathsContainer")
    private val currentActionText: Text by fxid("currentAction")
    private val currentOrderLabel: Label by fxid("currentOrder")
    private val tableContainer: ScrollPane by fxid("tableContainer")
    private val rect: Button by fxid("rect")
    private val rightButton: Button by fxid("rightButton")
    private val leftButton: Button by fxid("leftButton")
    private val randomGraphBuild : MenuItem by fxid("randomGraphBuildButton")
    private val buildGraphFromFileButton: MenuItem by fxid("buildGraphFromFileButton")
    init {

        var graphPane = pane()
        var graph: Graph?
        val p = Painter()
        val gController =  GraphController()

        setButtonAnimation(leftButton, Pair(70.0, 51.0), Pair(306.0, 631.0), Pair(72.0, 53.0), Pair(305.0, 630.0))
        setButtonAnimation(rightButton, Pair(70.0, 51.0), Pair(493.0, 631.0), Pair(72.0, 53.0), Pair(492.0, 630.0))
        setButtonAnimation(rect, Pair(70.0, 51.0), Pair(398.0, 631.0), Pair(72.0, 53.0), Pair(397.0, 630.0))

        currentOrderLabel.text = "Очередь: ${order.joinToString(", ")}"
        currentActionText.text = currentAction;

        foundPaths.forEach {
            vbox.add(label(it))
        }

        tableContainer.add(createTable())

        randomGraphBuild.setOnAction{

            graph = Graph(graphType = GraphType.RandomGraph)
            graphPane.clear()
            val copy = graph!!
            graphPane = p.paintGraph(copy)
            graphPane.layoutY = 20.0
            root.add(graphPane)
        }
        buildGraphFromFileButton.setOnAction{
            graph = gController.buildFromFile()
            if (graph != null) {
                graphPane.clear()
                val copy = graph!!
                graphPane = p.paintGraph(copy)
                graphPane.layoutY = 20.0
                root.add(graphPane)
            }
        }
    }

    private fun createTable(): TableView<Row> {
        return tableview(columns) {
            maxHeight = 498.0
            maxWidth = 598.0
            style = "-fx-background-color: none;"
            selectionModel = null
            vertexes.forEachIndexed { index, it ->
                readonlyColumn(it, Row::array) {
                    value { it.value.array[index] }
                    isResizable = false
                    prefWidth = 50.0
                    isSortable = false
                }
            }
        }
    }

    private fun setButtonAnimation(button: Button, pressedSize: Pair<Double, Double>, pressedLayout: Pair<Double, Double>,
                                   releasedSize: Pair<Double, Double>, releasedLayout: Pair<Double, Double>) {
        button.setOnMousePressed {
            button.setPrefSize(pressedSize.first, pressedSize.second)
            button.layoutX = pressedLayout.first
            button.layoutY = pressedLayout.second
        }
        button.setOnMouseReleased {
            button.setPrefSize(releasedSize.first, releasedSize.second)
            button.layoutX = releasedLayout.first
            button.layoutY = releasedLayout.second
        }
    }
}

