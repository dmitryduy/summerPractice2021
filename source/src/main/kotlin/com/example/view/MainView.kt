package com.example.view

import com.example.dijkstra.*
import com.example.graph.*
import tornadofx.*
import com.example.painter.*

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TableView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import java.util.*


class Row(val array: List<Any>)


val vertexes = observableListOf<String>("A", "B", "C", "D", "E", "F", "J", "H", "I", "J", "K", "L")

val foundPaths = observableListOf<String>(
    "A->B: AB",
    "A->C: ABC",
    "A->E: ABCDE",
    "A->B: AB",
    "A->C: ABC",
    "A->E: ABCDE",
    "A->B: AB",
    "A->C: ABC",
    "A->E: ABCDE",
    "A->B: AB",
    "A->C: ABC",
    "A->E: ABCDE"
)
val currentAction = "Какое-то действие, которое проиходит прямо сейчас"
val order = observableListOf<String>("A", "B", "C", "D", "E")


class MainView : View("Алгоритм Дейкстры") {
    override val root: AnchorPane by fxml("layout.fxml")
    private val vbox: VBox by fxid("foundPathsContainer")
    private val currentActionText: Text by fxid("currentAction")
    private val currentOrderLabel: Label by fxid("currentOrder")
    private val tableContainer: ScrollPane by fxid("tableContainer")
    private val rect: Button by fxid("rect")
    private val rightButton: Button by fxid("rightButton")
    private val leftButton: Button by fxid("leftButton")
    private lateinit var temp: DijkstraSteps
    private var currentStep = -1


    init {

        var g = Graph(graphType = GraphType.RandomGraph)
        var p = Painter()

        val a = Dijkstra()
        temp = a.makeAlgorithm(g, g.getVertices()[3])//возвращает Dijkstrasteps()

        val graphPane = p.paintGraph(g)
        graphPane.layoutY = 20.0
        root.add(graphPane)

        setButtonAnimation(leftButton, Pair(70.0, 51.0), Pair(306.0, 631.0), Pair(72.0, 53.0), Pair(305.0, 630.0))
        setButtonAnimation(rightButton, Pair(70.0, 51.0), Pair(493.0, 631.0), Pair(72.0, 53.0), Pair(492.0, 630.0))
        setButtonAnimation(rect, Pair(70.0, 51.0), Pair(398.0, 631.0), Pair(72.0, 53.0), Pair(397.0, 630.0))

        rightButton.setOnMouseClicked {
            if (currentStep < temp.dijkstraSteps.size - 1)
                currentStep++
            changeInterface(currentStep)
        }

        leftButton.setOnMouseClicked {
            if (currentStep > 0)
                currentStep--
            changeInterface(currentStep)
        }


        //пример работы
        /*val a = Dijkstra()
        val temp = a.makeAlgorithm(g, g.getVertices()[3])//возвращает Dijkstrasteps()
        val test = temp.dijkstraSteps
        for (i in test) {
            when (i.getState()) {
                DijkstraState.Start -> {
                    print("Start")
                    val table = i.getTable()
                    for (j in table) {
                        for (k in j) {
                            if (k.first == null) {
                                print("" + "inf" + " ")
                            } else {
                                print("" + k + " ")
                            }
                        }
                        println()
                    }
                    val queue = i.getQueue()
                    for (i in queue) {
                        if (i.second == Integer.MAX_VALUE) {
                            print("(" + i.first.getValue() + " " + "inf" + ") ")
                        } else {
                            print("(" + i.first.getValue() + " " + i.second + ") ")
                        }
                    }

                    val currVertex = i.getCurrVertex()
                    println("")
                    print("StartVertex:" + currVertex.getValue())//типа здесь нужна нарисовать вершины
                }
                DijkstraState.VertexProcessing -> {//здесь нужно подсветить ребра
                    println("VertexProcessing")
                    val currVertex = i.getCurrVertex()
                    print("CurrVertex:" + currVertex.getValue())
                }
                DijkstraState.UpdatedPath -> {
                    println("UpdatedPath")
                    var paths = i.getPaths()
                    for (i in paths) {
                        println(i)
                    }

                }
                DijkstraState.UpdatedQueue -> {
                    println("UpdatedQueue")
                    val queue = i.getQueue()
                    val sortedQueue = queue.sortedWith(MyComparator())//нужно отсортировать
                    for (i in sortedQueue) {
                        if (i.second == Integer.MAX_VALUE) {
                            print("(" + i.first.getValue() + " " + "inf" + ") ")
                        } else {
                            print("(" + i.first.getValue() + " " + i.second + ") ")
                        }
                    }
                }
                DijkstraState.UpdatedTable -> {
                    println("UpdatedTable")
                    val table = i.getTable()
                    for (j in table) {
                        for (k in j) {
                            //  print(k)
                            if (k.first == null && k.second == Integer.MAX_VALUE) {
                                print("" + "inf" + " ")
                            } else if (k.first != null) {
                                print("" + k.second + " ")
                            } else {
                                print("" + "--" + " ")
                            }
                        }
                        println()
                    }
                }
                else -> print("\n")
            }
            println("\n")
        }//проходим по шагам и обновляем в зависимости от шага
        println("end")*/
    }

    private fun updateEveryStep(currentStepInfo: DijkstraStep) {
        setQueue(currentStepInfo)

        val paths = currentStepInfo.getPaths()
        vbox.clear()
        paths.forEach {
            vbox.add(label(it))
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
                    currentRow.add(item)
                }
            }
            columns.add(Row(currentRow))
        }

        tableContainer.add(tableview(columns) {
            maxHeight = 498.0
            maxWidth = 598.0
            style = "-fx-background-color: none;"
            selectionModel = null
            vertexes.forEachIndexed{ index, it ->
                readonlyColumn(it, Row::array) {
                        value { it.value.array[index] }
                    isResizable = false
                    prefWidth = 50.0
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
}

