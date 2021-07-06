package com.example.view

import com.example.dijkstra.*
import com.example.graph.*
import tornadofx.*
import com.example.painter.*
import javafx.scene.control.*

import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import java.util.*
import kotlin.collections.ArrayList


class Row(val array: List<Any>)


val vertexes = observableListOf<Vertex>()
var isByStepStarted = false


class MainView : View("Алгоритм Дейкстры") {
    override val root: AnchorPane by fxml("layout.fxml")
    private val vbox: VBox by fxid("foundPathsContainer")
    private val currentActionText: Text by fxid("currentAction")
    private val currentOrderLabel: Label by fxid("currentOrder")
    private val tableContainer: ScrollPane by fxid("tableContainer")
    private val rect: Button by fxid("rect")
    private val rightButton: Button by fxid("rightButton")
    private val leftButton: Button by fxid("leftButton")
    private val startByStep: MenuItem by fxid("startByStep")
    private val foundPathLabel: Label by fxid("foundPathLabel")
    private val foundPathScroll: ScrollPane by fxid("foundPathScroll")
    private val currentActionLabel: Label by fxid("currentActionLabel")
    private lateinit var temp: DijkstraSteps
    private var arrayPaths: ArrayList<String> = ArrayList()//пути рассчитываются один раз
    private var countPaths: Int = 0//количество путей на текущем шаге
    private var currentStep = -1


    init {

        var g = Graph(graphType = GraphType.RandomGraph)
        var p = Painter()

        val a = Dijkstra()
        temp = a.makeAlgorithm(g, g.getVertices()[0])//возвращает Dijkstrasteps()
        g.getVertices().forEach { vertexes.add(it) }
        arrayPaths = getPaths(temp)


        val graphPane = p.paintGraph(g)
        graphPane.layoutY = 20.0
        root.add(graphPane)

        setButtonAnimation(leftButton, Pair(70.0, 51.0), Pair(306.0, 631.0), Pair(72.0, 53.0), Pair(305.0, 630.0))
        setButtonAnimation(rightButton, Pair(70.0, 51.0), Pair(493.0, 631.0), Pair(72.0, 53.0), Pair(492.0, 630.0))
        setButtonAnimation(rect, Pair(70.0, 51.0), Pair(398.0, 631.0), Pair(72.0, 53.0), Pair(397.0, 630.0))

        rightButton.setOnMouseClicked {

            if (currentStep < temp.dijkstraSteps.size - 1)
                currentStep++
            if (temp.dijkstraSteps[currentStep].getState() == DijkstraState.UpdatedPath && countPaths != arrayPaths.size) {
                countPaths++
            }
            changeInterface(currentStep)


        }

        leftButton.setOnMouseClicked {

            if (currentStep > 0)
                currentStep--
            if (temp.dijkstraSteps[currentStep + 1].getState() == DijkstraState.UpdatedPath && countPaths != 0) {
                countPaths--
            }
            changeInterface(currentStep)


        }

        startByStep.setOnAction {
            activateButtons()
            currentStep++
            changeInterface(currentStep)
        }


    }

    private fun getPaths(dijkstraSteps: DijkstraSteps): ArrayList<String> {
        arrayPaths.clear()
        val vertexs: ArrayList<Vertex> = ArrayList<Vertex>()

        vertexes.forEach {
            if (it.getValue() != "D") {
                val curr = dijkstraSteps.getResult().getPath(it)
                if (curr != "=") {
                    arrayPaths.add(curr)
                }
            }
        }
        val sortedList = arrayPaths.sortedWith(MyComparatorPaths())
        arrayPaths.clear()
        for (i in sortedList) {
            arrayPaths.add(i)
        }
        return arrayPaths
    }

    private fun updateEveryStep(currentStepInfo: DijkstraStep) {
        setQueue(currentStepInfo)

        vbox.clear()
        if (countPaths != 0) {
            for (i in 0..countPaths - 1) {
                vbox.add(label(arrayPaths[i]))
            }
        }
        setTable(currentStepInfo)
    }

    private fun setTable(currentStepInfo: DijkstraStep) {
        val columns = observableListOf<Row>()
        val table = currentStepInfo.getTable()
        table.forEach { row ->
            val currentRow = mutableListOf<Any>()
            row.forEach { item ->
                // как будет выглядеть конкретная клетка в таблице
                if (item.first == null && item.second == Integer.MAX_VALUE) currentRow.add("inf")
                else if (item.first == null) {
                    currentRow.add("--")
                } else {
                    currentRow.add("${item.second}(${item.first})")
                }
            }
            columns.add(Row(currentRow))
        }

        tableContainer.add(tableview(columns) {
            maxHeight = 498.0
            maxWidth = 598.0
            style = "-fx-background-color: none;"
            selectionModel = null
            vertexes.forEachIndexed { index, it ->
                readonlyColumn(it.getValue(), Row::array) {

                    value {
                        it.value.array[index]
                    }
                    isResizable = false
                    prefWidth = 70.0
                    isSortable = false
                }
            }
        })
    }

    private fun setQueue(currentStepInfo: DijkstraStep, sortable: Boolean = false) {
        val endQueue: Any
        endQueue = if (!sortable) currentStepInfo.getQueue()
        else currentStepInfo.getQueue().sortedWith(MyComparator())

        var actionText: String = "Очередь: "

        // как будет выглядеть текст очереди
        endQueue.forEach {
            actionText += if (it.second == Integer.MAX_VALUE) "(${it.first.getValue()} inf)"
            else "(${it.first.getValue()} ${it.second})"
        }
        currentOrderLabel.text = actionText
    }

    private fun setButtonAnimation(
        button: Button, pressedSize: Pair<Double, Double>, pressedLayout: Pair<Double, Double>,
        releasedSize: Pair<Double, Double>, releasedLayout: Pair<Double, Double>
    ) {
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

    private fun changeInterface(currentStep: Int) {
        val currentStepInfo = temp.dijkstraSteps[currentStep]

        when (currentStepInfo.getState()) {
            DijkstraState.Start -> initInterface(currentStepInfo)

            DijkstraState.VertexProcessing -> updateVertex(currentStepInfo)

            DijkstraState.UpdatedPath -> updatePath(currentStepInfo)

            DijkstraState.UpdatedQueue -> updateQueue(currentStepInfo)

            DijkstraState.UpdatedTable -> updateTable(currentStepInfo)
        }
    }

    private fun initInterface(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        // Устанавливает текущее действие, в данном случае start
        currentActionText.text = "Start"

        setQueue(currentStepInfo)

        val currentVertex = currentStepInfo.getCurrVertex()
        // к текущему действию прибавляется запись о начальной вершине
        currentActionText.text += "\nStartVertex: $currentVertex"
    }

    private fun updateVertex(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        val currentVertex = currentStepInfo.getCurrVertex()
        // обновляется текущее действие
        currentActionText.text = "CurrVertex: ${currentVertex.getValue()}"
    }

    private fun updatePath(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        // обновляется текущее действие
        currentActionText.text = "UpdatedPath"
    }

    private fun updateQueue(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        // обновляется текущее действие
        currentActionText.text = "UpdatedQueue"
        setQueue(currentStepInfo, true)
    }

    private fun updateTable(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        // обновляется текущее действие
        currentActionText.text = "UpdatedTable"
    }

    private fun activateButtons() {
        rightButton.isDisable = false
        leftButton.isDisable = false
        rect.isDisable = false
    }
}

