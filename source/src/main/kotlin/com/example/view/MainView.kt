package com.example.view

import com.example.dijkstra.*
import com.example.graph.*
import com.example.graphcontroller.*
import com.example.layout.Layout
import tornadofx.*
import com.example.painter.*
import com.example.visualised.*
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.*

import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class Row(val array: List<Any>)


val vertexes = observableListOf<Vertex>()
var isByStepStarted = false
var isAutoplayStarted = false
var clearTimer = false
var byButton = false
var firstLoad = false
const val DEBOUNCE_TIME: Long = 200
const val CHANGE_STEP_TIME: Long = 500


class MainView : View("Алгоритм Дейкстры") {
    override val root: AnchorPane by fxml("layout.fxml")
    private val vbox: VBox by fxid("foundPathsContainer")
    private val currentActionText: TextField by fxid("currentAction")
    private val currentOrderLabel: TextField by fxid("currentOrder")
    private val tableContainer: ScrollPane by fxid("tableContainer")
    private val rect: Button by fxid("rect")
    private val rightButton: Button by fxid("rightButton")
    private val leftButton: Button by fxid("leftButton")
    private val startByStep: MenuItem by fxid("startByStep")
    private val randomGraphBuildButton: MenuItem by fxid("randomGraphBuildButton")
    private val buildGraphFromFileButton: MenuItem by fxid("buildGraphFromFileButton")
    private val saveToFileButton: MenuItem by fxid("saveToFileButton")
    private val setGraphError: Label by fxid("setGraphError")
    private val autoplay: MenuItem by fxid("autoplay")
    private val startAlgorithmContainer: Menu by fxid("startAlgorithmContainer")
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

    init {
        layout.stylizeTextField(currentOrderLabel)
        layout.stylizeTextField(currentActionText)

        layout.setButtonAnimation(leftButton)
        layout.setButtonAnimation(rightButton)
        layout.setButtonAnimation(rect)


        root.add(graphController.wholePane)

        deleteVertexButton.setOnAction {
            graphController.state = GraphControllerState.DELETINGVERTEX
        }
        addVertexButton.setOnAction{
            graphController.state = GraphControllerState.ADDINGVERTEX
        }
        addEdgeButton.setOnAction{
            graphController.state = GraphControllerState.CHOOSINGFIRSTVERTEX
        }
        deleteEdgeButton.setOnAction{
            graphController.state = GraphControllerState.DELETINGEDGE
        }

        rightButton.setOnMouseClicked {

            leftButton.isDisable = false
            if (layout.getStep() < temp.dijkstraSteps.size - 1) {
                rightButton.isDisable = true
                thread {
                    Thread.sleep(DEBOUNCE_TIME)
                    rightButton.isDisable = false
                }
                layout.incrementStep()
            }

            if (layout.getStep() == temp.dijkstraSteps.size - 1) {
                rightButton.isDisable = true
                graphController.state = GraphControllerState.NOTEDITING
            }
            if (temp.dijkstraSteps[layout.getStep()].getState() == DijkstraState.UpdatedPath && countPaths != arrayPaths.size) {
                countPaths++
            }
            changeInterface()


        }

        rect.setOnMouseClicked {
            rect.isDisable = true
            thread {
                Thread.sleep(DEBOUNCE_TIME)
                rect.isDisable = false
            }
            if (byButton || firstLoad) {
                firstLoad = false
                byButton = false
                rect.style = "-fx-background-color: red;"
                setInterval()
            } else {
                byButton = true
                clearTimer = true
                rect.style = "-fx-shape: 'M32 32 L32 44 L42 38';-fx-background-color: green"
            }

        }

        leftButton.setOnMouseClicked {

            rightButton.isDisable = false

            if (layout.getStep() > 0) {
                leftButton.isDisable = true
                thread {
                    Thread.sleep(DEBOUNCE_TIME)
                    leftButton.isDisable = false
                }

                layout.decrementStep()
            }

            if (layout.getStep() == 0) {
                leftButton.isDisable = true
            }
            if (temp.dijkstraSteps[layout.getStep() + 1].getState() == DijkstraState.UpdatedPath && countPaths != 0) {
                countPaths--
            }
            changeInterface()
        }

        startByStep.setOnAction {
            setGraphError.isVisible = !graphController.graphIsSet
            if (graphController.graph != null && graphController.graphIsSet){
                val d = Dijkstra()
                temp = d.makeAlgorithm(graphController.graph!!, graphController.graph!!.getVertices()[0])
                graphController.graph!!.getVertices().forEach {vertexes.add(it)}
                arrayPaths = getPaths(temp)
            }
            if (!isByStepStarted && graphController.graphIsSet) {
                isByStepStarted = true
                graphController.state = GraphControllerState.RUNNING_ALGORITHM
                startAlgorithmContainer.isDisable = true

                rightButton.isDisable = false
                layout.incrementStep()
                changeInterface()
            }

        }

        autoplay.setOnAction {
            setGraphError.isVisible = !graphController.graphIsSet
            if (graphController.graph != null && graphController.graphIsSet){
                val d = Dijkstra()
                temp = d.makeAlgorithm(graphController.graph!!, graphController.graph!!.getVertices()[0])
                graphController.graph!!.getVertices().forEach {vertexes.add(it)}
                arrayPaths = getPaths(temp)
            }
            if (!isAutoplayStarted && graphController.graphIsSet) {
                clearTimer = false
                startAlgorithmContainer.isDisable = true
                isAutoplayStarted = true
                rect.isDisable = false
                graphController.state = GraphControllerState.RUNNING_ALGORITHM
                layout.incrementStep()
                changeInterface()
                firstLoad = true

            }
        }

        buildGraphFromFileButton.setOnAction {
            rect.isDisable = true
            rect.style = "-fx-shape: 'M32 32 L32 44 L42 38';-fx-background-color: green"
            graphController.buildFromFile(graphController.getFileNameFromDialog())
            if (isAutoplayStarted)
                clearTimer = true
            clearLayout()
            buildGraph(graphController)
        }

        randomGraphBuildButton.setOnAction {
            rect.isDisable = true
            rect.style = "-fx-shape: 'M32 32 L32 44 L42 38';-fx-background-color: green"
            if (isAutoplayStarted)
                clearTimer = true
            clearLayout()
            graphController.randomBuild()
            buildGraph(graphController)
        }

        saveToFileButton.setOnAction{
            if (graphController.graph != null && graphController.graph?.getVertices()?.size != 0)
                graphController.saveToFile()
        }
    }

    private fun setInterval() {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (layout.getStep() == temp.dijkstraSteps.size - 1 || clearTimer) {
                    clearTimer = false
                    if (layout.getStep() == temp.dijkstraSteps.size - 1) {
                        isAutoplayStarted = false
                        rect.isDisable = true
                        graphController.state = GraphControllerState.NOTEDITING
                    }


                    this.cancel()
                } else {
                    Platform.runLater(Runnable() {
                        layout.incrementStep()
                        if (temp.dijkstraSteps[layout.getStep()].getState() == DijkstraState.UpdatedPath && countPaths != arrayPaths.size) {
                            countPaths++
                        }

                        changeInterface()


                    })
                }

            }

        }, 0, CHANGE_STEP_TIME)
    }

    private fun clearLayout() {
        byButton = false
        startAlgorithmContainer.isDisable = false
        setGraphError.isVisible = false
        graphController.graphIsSet = true
        if (root.getChildList()?.size == 2) {
            root.getChildList()?.remove(root.getChildList()?.get(1))
        }
        vertexes.clear()
        if (tableContainer.content != null) {
            tableContainer.content = null
        }
        layout.resetStep()
        countPaths = 0
        leftButton.isDisable = true
        rightButton.isDisable = true
        isByStepStarted = false
        isAutoplayStarted = false
        vbox.clear()
        currentOrderLabel.text = ""
        currentActionText.text = ""


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

        vbox.clear()
        if (countPaths != 0) {
            for (i in 0 until countPaths) {
                val textField = TextField(arrayPaths[i])
                layout.stylizeTextField(textField)
                vbox.add(textField)
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
        var actionText = "Очередь: "
        if (!sortable) {
            val queue = currentStepInfo.getQueue()
            queue.forEach {
                actionText += if (it.second == Integer.MAX_VALUE) "(${it.first.getValue()} inf)"
                else "(${it.first.getValue()} ${it.second})"
            }
        } else {
            val tmp = currentStepInfo.getQueue()
            val queue = tmp.sortedWith(MyComparator())
            queue.forEach {
                actionText += if (it.second == Integer.MAX_VALUE) "(${it.first.getValue()} inf)"
                else "(${it.first.getValue()} ${it.second})"
            }
        }

        currentOrderLabel.text = actionText
    }


    private fun changeInterface() {
        val currentStepInfo = temp.dijkstraSteps[layout.getStep()]
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
        // Устанавливает текущее действие, в данном случае start

        setQueue(currentStepInfo)

        val currentVertex = currentStepInfo.getCurrVertex()
        // к текущему действию прибавляется запись о начальной вершине

    }

    private fun updateVertex(currentStepInfo: DijkstraStep){
        updateEveryStep(currentStepInfo)
        val currentVertex = currentStepInfo.getCurrVertex()
        // обновляется текущее действие
		
		//------подсветка текущей вершины и ее соседей
        var noQueueList = mutableListOf<VisualisedEdge>()
        var inQueueList = mutableListOf<VisualisedEdge>()
        var queueList = mutableListOf<Vertex>()
        graphController.restoreEdgesStyles()
        graphController.restoreVerticesStyle()
        currentStepInfo.getQueue().forEach{
            queueList.add(it.first)
        }
        graphController.visualEdges.forEach{
            if (it.edge.first.getValue() == currentVertex.getValue()){
                if (it.edge.second in queueList){

                    inQueueList.add(it)
                }
                else{
                    noQueueList.add(it)
                }
            }
        }
        graphController.highlightVertices(listOf(graphController.getVisualVertex(currentVertex)!!), Color.LIGHTGREEN)
        graphController.highlightEdges(inQueueList, Color.LIGHTGREEN, Color.DARKGREEN)
        graphController.highlightEdges(noQueueList, Color.LIGHTGRAY, Color.DARKGRAY)
		//----------
    }

    private fun updatePath(currentStepInfo: DijkstraStep){
        updateEveryStep(currentStepInfo)
        // обновляется текущее действие
        currentActionText.text += arrayPaths[countPaths-1]
		
        //-----подсветка ребер и вершин входящих в найденный путь
        graphController.restoreVerticesStyle()
        graphController.restoreEdgesStyles()
        var eList = mutableListOf<VisualisedEdge>()
        var vList = mutableListOf<Vertex>()
        var initial = currentStepInfo.getPath(currentStepInfo.getCurrVertex()).split("->")
        initial.forEach{
                val v = graphController.graph!!.getVertex(it.substringBeforeLast("="))
                if (v != null)
                vList.add(v)
        }
            for (h in 0 until vList.size - 1){
            eList.add(graphController.getVisualEdge(vList[h], vList[h + 1])!!)
        }
        val vvList = mutableListOf<VisualisedVertex>()
        vList.forEach{
            vvList.add(graphController.getVisualVertex(it)!!)
        }
        graphController.highlightEdges(eList, Color.GREEN, Color.DARKGREEN)
        graphController.highlightVertices(vvList, Color.GREEN)
		//--------
    }

    private fun updateQueue(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        // обновляется текущее действие
        setQueue(currentStepInfo, true)
    }

    private fun updateTable(currentStepInfo: DijkstraStep) {
        updateEveryStep(currentStepInfo)
        // обновляется текущее действие
    }


    private fun buildGraph(controller: GraphController) {

        val graph = controller.graph
        if (graph != null){
            val graphPane = controller.wholePane
            graphPane.layoutY = 30.0
            root.add(graphPane)
        }
        else {
            graphController.graphIsSet = false
            setGraphError.isVisible = true
        }

    }
}

