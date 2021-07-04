package com.example.view

import com.example.graph.*
import tornadofx.*
import com.example.painter.*
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TableView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text


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

    init {
        var g = Graph(graphType = GraphType.RandomGraph)
        var p = Painter()
        root.add(p.paintGraph(g))

        currentOrderLabel.text = "Очередь: ${order.joinToString(", ")}"
        currentActionText.text = currentAction;

        foundPaths.forEach {
            vbox.add(label(it))
        }

        tableContainer.add(createTable())

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
}

