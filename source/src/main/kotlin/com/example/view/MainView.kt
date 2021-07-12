

package com.example.view

import com.example.dijkstra.*
import com.example.graph.*
import com.example.graphcontroller.*
import com.example.layout.Layout
import tornadofx.*
import com.example.painter.*
import com.example.visualised.*
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*

import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class Row(val array: List<Any>)


val vertexes = observableListOf<Vertex>()
var isByStepStarted = false
var isautoplayMenuButtonStarted = false
var clearTimer = false
var byButton = false
var firstLoad = false
//reset alogrithm after autoplay finished
var resetAlgorithm = false
var startVertex = 0

const val CHANGE_STEP_TIME: Long = 500
const val PAUSE_BUTTON_STYLES = "-fx-shape: 'M32 32 L32 44 L42 38';-fx-background-color: #10a341"
const val RECORD_BUTTON_STYLES = "-fx-background-color: #f33535;-fx-shape: 'M44 0 L32 0 L32 64 L52 64 L52 0 L64 0 L64 64 L44 64'"



class MainView : View("Алгоритм Дейкстры") {
    override val root: GridPane by fxml("layout.fxml")
    private val foundPathsText: TextArea by fxid("foundPathsContainer")
    private val graphContainer: Pane by fxid("graphContainer")
    private val currentActionText: TextArea by fxid("currentAction")
    private val currentOrderLabel: TextField by fxid("currentOrder")
    private val tableContainer: ScrollPane by fxid("tableContainer")
    private val pauseButton: Button by fxid("pauseButton")
    private val nextStepButton: Button by fxid("nextStepButton")
    private val prevStepButton: Button by fxid("prevStepButton")
    private val stepByStepButton: MenuItem by fxid("stepByStepButton")
    private val randomGraphBuildButton: MenuItem by fxid("randomGraphBuildButton")
    private val buildGraphFromFileButton: MenuItem by fxid("buildGraphFromFileButton")
    private val saveToFileButton: MenuItem by fxid("saveToFileButton")
    private val setGraphError: Label by fxid("setGraphError")
    private val autoplayMenuButton: MenuItem by fxid("autoplayMenuButton")
    private val startAlgorithmMenuButton: Menu by fxid("startAlgorithmMenuButton")
    private val chooseVertexButton: MenuItem by fxid("chooseVertexButton")
    private val chooseVertexContainer: Menu by fxid("chooseVertexContainer")
    private lateinit var temp: DijkstraSteps
    private var arrayPaths: ArrayList<String> = ArrayList()//пути рассчитываются один раз
    private var countPaths: Int = 0//количество путей на текущем шаге
    private var currentStep = -1
    private val layout = Layout()
    private val deleteVertexButton: MenuItem by fxid("deleteVertexButton")
    private val addVertexButton: MenuItem by fxid("addVertexButton")
    private val addEdgeButton: MenuItem by fxid("addEdgeButton")
    private val deleteEdgeButton: MenuItem by fxid("deleteEdgeButton")
    private val graphController = GraphController()
    private var alreadyFoundToColor = mutableListOf<VisualisedVertex>()

    init {
        layout.writeLogs("Запущена программа")
        layout.stylizeTextField(currentOrderLabel)
        layout.stylizeTextArea(currentActionText)
        layout.stylizeTextArea(foundPathsText)

        layout.setButtonAnimation(prevStepButton)
        layout.setButtonAnimation(nextStepButton)
        layout.setButtonAnimation(pauseButton)


        graphContainer.add(graphController.wholePane)
        graphController.wholePane.maxWidthProperty().bind(graphContainer.widthProperty() - 10)
        graphController.wholePane.maxHeightProperty().bind(graphContainer.heightProperty())
        primaryStage.minWidthProperty().bind((graphController.wholePane.widthProperty() + 108) * 1.597)
        graphContainer.minHeightProperty().bind(graphController.wholePane.heightProperty())
        primaryStage.minHeightProperty().bind((graphController.wholePane.heightProperty() + 50) * 1.4)

        deleteVertexButton.setOnAction {
            graphController.state = GraphControllerState.DELETINGVERTEX
            layout.writeLogs("Удалена вершина")
        }
        addVertexButton.setOnAction {
            graphController.state = GraphControllerState.ADDINGVERTEX
            layout.writeLogs("Добавлена вершина")
        }
        addEdgeButton.setOnAction {
            graphController.state = GraphControllerState.CHOOSINGFIRSTVERTEX
            layout.writeLogs("Добавлено ребро")
        }
        deleteEdgeButton.setOnAction {
            graphController.state = GraphControllerState.DELETINGEDGE
            layout.writeLogs("Удалено ребро")
        }

        nextStepButton.setOnMouseClicked {
            prevStepButton.isDisable = false
            if (currentStep < temp.dijkstraSteps.size - 1) {
                layout.writeLogs("Переход на следующий шаг")
                currentStep++
            }

            if (currentStep == temp.dijkstraSteps.size - 1) {
                nextStepButton.isDisable = true
                graphController.state = GraphControllerState.NOTEDITING
            }
            if (temp.dijkstraSteps[currentStep].getState() == DijkstraState.UpdatedPath && countPaths != arrayPaths.size) {
                countPaths++
            }
            changeInterface()


        }

        pauseButton.setOnMouseClicked {

            if (byButton || firstLoad) {
                if (!firstLoad)
                    layout.writeLogs("Автоматическая визуализация графа запущена снова")
                firstLoad = false
                byButton = false
                pauseButton.style = RECORD_BUTTON_STYLES
                setInterval()
            } else {
                layout.writeLogs("Автоматическая визуализация графа остановлена")
                byButton = true
                clearTimer = true
                pauseButton.style = PAUSE_BUTTON_STYLES
            }

        }

        prevStepButton.setOnMouseClicked {

            nextStepButton.isDisable = false
            graphController.state = GraphControllerState.RUNNING_ALGORITHM
            graphController.restoreEdgesStyles()
            graphController.restoreVerticesStyle()
            if (currentStep == 1) {
                prevStepButton.isDisable = true
            }
            if (currentStep > 0 ) {
                layout.writeLogs("Переход на предыдущий шаг")
               currentStep--
                if (temp.dijkstraSteps[currentStep + 1].getState() == DijkstraState.UpdatedPath && countPaths != 0) {
                    countPaths--
                }
                changeInterface()
            }

        }

        stepByStepButton.setOnAction {
            if (resetAlgorithm) {
                clearLayout()
            }
            alreadyFoundToColor.clear()
            setGraphError.isVisible = !graphController.graphIsSet
            if (graphController.graph != null && graphController.graphIsSet) {
                layout.writeLogs("Запущена пошаговая визуализация")
                val d = Dijkstra()
                temp = d.makeAlgorithm(graphController.graph!!, graphController.graph!!.getVertices()[startVertex])
                vertexes.clear()
                graphController.graph!!.getVertices().forEach { vertexes.add(it) }
                arrayPaths = getPaths(temp)
            }
            if (!isByStepStarted && graphController.graphIsSet) {
                isByStepStarted = true
                graphController.state = GraphControllerState.RUNNING_ALGORITHM
                graphController.restoreEdgesStyles()
                graphController.restoreVerticesStyle()
                startAlgorithmMenuButton.isDisable = true

                nextStepButton.isDisable = false
                currentStep++
                changeInterface()
            }

        }

        autoplayMenuButton.setOnAction {
            if (resetAlgorithm) {
                clearLayout()
            }
            alreadyFoundToColor.clear()
            setGraphError.isVisible = !graphController.graphIsSet
            if (graphController.graph != null && graphController.graphIsSet) {
                val d = Dijkstra()
                temp = d.makeAlgorithm(graphController.graph!!, graphController.graph!!.getVertices()[startVertex])
                vertexes.clear()
                graphController.graph!!.getVertices().forEach { vertexes.add(it) }
                arrayPaths = getPaths(temp)
            }
            if (!isautoplayMenuButtonStarted && graphController.graphIsSet) {
                layout.writeLogs("Запущена автоматическая визуализация")
                clearTimer = false
                startAlgorithmMenuButton.isDisable = true
                isautoplayMenuButtonStarted = true
                pauseButton.isDisable = false
                graphController.state = GraphControllerState.RUNNING_ALGORITHM
                graphController.restoreEdgesStyles()
                graphController.restoreVerticesStyle()
                currentStep++
                changeInterface()
                firstLoad = true

            }
        }

        buildGraphFromFileButton.setOnAction {
            pauseButton.isDisable = true
            pauseButton.style = PAUSE_BUTTON_STYLES
            graphController.buildFromFile(graphController.getFileNameFromDialog())
            if (isautoplayMenuButtonStarted)
                clearTimer = true
            clearLayout(true)
            buildGraph(graphController)
        }

        randomGraphBuildButton.setOnAction {
            pauseButton.isDisable = true
            pauseButton.style = PAUSE_BUTTON_STYLES
            if (isautoplayMenuButtonStarted)
                clearTimer = true
            clearLayout(true)
            graphController.randomBuild()
            buildGraph(graphController)
        }

        saveToFileButton.setOnAction {
            if (graphController.graph != null && graphController.graph?.getVertices()?.size != 0) {
                layout.writeLogs("Сохранение графа")
                graphController.saveToFile()
            }
        }

        chooseVertexButton.setOnAction {
            vertexes.clear()
            graphController.graph!!.getVertices().forEach { vertexes.add(it) }
            val dialog = TextInputDialog()
            dialog.headerText = ""
            dialog.contentText = "Введите имя вершины"
            val vDialog = dialog.showAndWait()
            val vertex = if (vDialog.isPresent) vDialog.get() else null
            var found = false
            if (vertex != null){
                vertexes.forEach {
                    if (it.getValue() == vertex) {
                        startVertex = it.getIndex()
                        found = true
                        chooseVertexContainer.isDisable = true
                        startAlgorithmMenuButton.isDisable = false
                    }
                }
                if (!found) {
                    val alert =  Alert(Alert.AlertType.ERROR, "Нет вершины $vertex. Проверьте правильность написания.")
                    alert.headerText = ""
                    alert.title = "Ошибка"
                    alert.show()
                }
                else {
                    val alert =  Alert(Alert.AlertType.INFORMATION, "Вершина $vertex установлена как начальная")
                    alert.headerText = ""
                    alert.title = "Установка вершины"
                    alert.show()
                }
            }
        }
    }

    private fun setInterval() {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (currentStep == temp.dijkstraSteps.size - 1 || clearTimer) {
                    clearTimer = false
                    if (currentStep == temp.dijkstraSteps.size - 1) {
                        isautoplayMenuButtonStarted = false
                        pauseButton.isDisable = true
                        graphController.state = GraphControllerState.NOTEDITING


                        startAlgorithmMenuButton.isDisable = false
                        resetAlgorithm = true
                        pauseButton.style = PAUSE_BUTTON_STYLES;
                    }


                    this.cancel()
                } else {
                    Platform.runLater(Runnable() {
                        currentStep++
                        if (temp.dijkstraSteps[currentStep].getState() == DijkstraState.UpdatedPath && countPaths != arrayPaths.size) {
                            countPaths++
                        }

                        changeInterface()
                    })
                }

            }

        }, 0, CHANGE_STEP_TIME)
    }

    private fun clearLayout(fromBuild: Boolean = false) {
        byButton = false
        startAlgorithmMenuButton.isDisable = false
        setGraphError.isVisible = false
        graphController.graphIsSet = true
        if (root.getChildList()?.size == 2) {
            root.getChildList()?.remove(root.getChildList()?.get(1))
        }
        vertexes.clear()

        if (tableContainer.content != null) {
            tableContainer.content = null
        }
        currentStep = -1
        countPaths = 0
        prevStepButton.isDisable = true
        nextStepButton.isDisable = true
        isByStepStarted = false
        isautoplayMenuButtonStarted = false
        foundPathsText.clear()
        currentOrderLabel.text = ""
        currentActionText.text = ""
        if (resetAlgorithm) {
            resetAlgorithm = false
        }
        else {
            graphContainer.clear()
        }


    }

    private fun getPaths(dijkstraSteps: DijkstraSteps): ArrayList<String> {
        arrayPaths.clear()

        vertexes.forEach {
            val curr = dijkstraSteps.getResult().getPath(it)
            if (curr != "=") {
                arrayPaths.add(curr)
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

        foundPathsText.clear()
        if (countPaths != 0) {
            for (i in 0 until countPaths) {
                foundPathsText.text+= "${arrayPaths[i]}\n"
            }
        }
        setTable(currentStepInfo)

    }

    private fun setTable(currentStepInfo: DijkstraStep) {
        val columns = observableListOf<Row>()
        val table = currentStepInfo.getTable()
        table.forEachIndexed { indexRow, row ->
            val currentRow = mutableListOf<Any>()
            row.forEachIndexed { index, item ->
                if (index == 0) {
                    currentRow.add(indexRow*4)
                }
                if (item.first == null && item.second == Integer.MAX_VALUE) currentRow.add("∞")
                else if (item.first == null) {
                    currentRow.add("--")
                } else {
                    currentRow.add("${item.second}(${item.first})")
                }
            }
            columns.add(Row(currentRow))

        }

        tableContainer.add(tableview(columns) {
            style = "-fx-background-color: none;"
            selectionModel = null


            vertexes.forEachIndexed { index, it ->
                if (index == 0) {
                    readonlyColumn("№Шага", Row::array) {
                        value {
                            it.value.array[0]
                        }
                        isResizable = false
                        prefWidth = 70.0
                        isSortable = false
                    }
                }
                readonlyColumn(it.getValue(), Row::array) {
                    value {
                        it.value.array[index + 1]
                    }
                    isResizable = false
                    prefWidth = 70.0
                    isSortable = false
                }
            }
        })
    }

    private fun setQueue(currentStepInfo: DijkstraStep, sortable: Boolean = false) {
        var actionText = "Очередь: "
        if (!sortable) {
            val queue = currentStepInfo.getQueue()
            queue.forEach {
                actionText += if (it.second == Integer.MAX_VALUE) "(${it.first.getValue()} ∞)"
                else "(${it.first.getValue()} ${it.second})"
            }
        } else {
            val tmp = currentStepInfo.getQueue()
            val queue = tmp.sortedWith(MyComparator())
            queue.forEach {
                actionText += if (it.second == Integer.MAX_VALUE) "(${it.first.getValue()} ∞)"
                else "(${it.first.getValue()} ${it.second})"
            }
        }

        currentOrderLabel.text = actionText
    }


    private fun changeInterface() {
        val currentStepInfo = temp.dijkstraSteps[currentStep]
        currentActionText.text = currentStepInfo.getMessage()

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

        setQueue(currentStepInfo)

        val currentVertex = currentStepInfo.getCurrVertex()

    }

    private fun updateVertex(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        val currentVertex = currentStepInfo.getCurrVertex()
        layout.writeLogs("Текущая вершина обновлена. Новая текущая вершина: $currentVertex")

        //------подсветка текущей вершины и ее соседей
        var noQueueList = mutableListOf<VisualisedEdge>()
        var inQueueList = mutableListOf<VisualisedEdge>()
        var queueList = mutableListOf<Vertex>()
        graphController.restoreEdgesStyles()
        graphController.restoreVerticesStyle(alreadyFoundToColor)
        currentStepInfo.getQueue().forEach {
            queueList.add(it.first)
        }
        graphController.visualEdges.forEach {
            if (it.edge.first.getValue() == currentVertex.getValue()) {
                if (it.edge.second in queueList) {

                    inQueueList.add(it)
                } else {
                    noQueueList.add(it)
                }
            }
        }
        graphController.highlightVertices(listOf(graphController.getVisualVertex(currentVertex)!!), Color.LIGHTGREEN)
        graphController.highlightEdges(inQueueList, Color.LIGHTGREEN, Color.DARKGREEN)
        graphController.highlightEdges(noQueueList, Color.LIGHTGRAY, Color.DARKGRAY)
        //----------
    }

    private fun updatePath(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        // обновляется текущее действие
        if (countPaths != 0) {
            currentActionText.text += arrayPaths[countPaths - 1]
        }
        //-----подсветка ребер и вершин входящих в найденный путь
        graphController.restoreVerticesStyle(alreadyFoundToColor)
        graphController.restoreEdgesStyles()
        val eList = mutableListOf<VisualisedEdge>()
        val vList = mutableListOf<Vertex>()
        val initial = currentStepInfo.getPath(currentStepInfo.getCurrVertex()).split("->")
        initial.forEach {
            val v = graphController.graph!!.getVertex(it.substringBeforeLast("="))
            if (v != null)
                vList.add(v)
        }
        for (h in 0 until vList.size - 1) {
            eList.add(graphController.getVisualEdge(vList[h], vList[h + 1])!!)
        }
        val vvList = mutableListOf<VisualisedVertex>()
        vList.forEach {
            vvList.add(graphController.getVisualVertex(it)!!)
        }
        graphController.highlightEdges(eList, Color.GREEN, Color.DARKGREEN)
        graphController.highlightVertices(vvList, Color.GREEN)
        if (vvList.isNotEmpty()){
            alreadyFoundToColor.add(vvList.last())
            graphController.highlightVertices(listOf(vvList.last()), Color.GREEN)
        }
        //--------

        layout.writeLogs("Найден новый путь: ${arrayPaths[countPaths - 1]}")
    }

    private fun updateQueue(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        // обновляется текущее действие
        setQueue(currentStepInfo, true)
        var text = ""
        currentStepInfo.getQueue().forEach {
            text += if (it.second == Integer.MAX_VALUE) "(${it.first} ∞)"
            else "(${it.first} ${it.second})"
        }
        layout.writeLogs("Очередь обновлена. Текущая очередь: $text")
    }

    private fun updateTable(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        layout.writeLogs("Таблица обновлена")
        // обновляется текущее действие
    }


    private fun buildGraph(controller: GraphController) {
        layout.writeLogs("Граф построен")
        val graph = controller.graph
        if (graph != null) {
            val graphPane = controller.wholePane
            graphContainer.add(graphPane)
            chooseVertexContainer.isDisable = false
            startAlgorithmMenuButton.isDisable = true
        } else {
            graphController.graphIsSet = false
            setGraphError.isVisible = true
        }
    }
}

