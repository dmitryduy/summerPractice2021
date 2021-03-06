package com.example.graphcontroller

import com.example.graph.*
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import java.io.File
import com.example.painter.*
import javafx.scene.control.Label
import javafx.scene.control.TextInputDialog
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.shape.*
import tornadofx.*
import java.io.InputStream
import com.example.visualised.*
enum class GraphControllerState{
    NOTEDITING, CHOOSINGFIRSTVERTEX, CHOOSINGSECONDVERTEX, ADDINGVERTEX, DELETINGEDGE, DELETINGVERTEX, RUNNING_ALGORITHM
}
class VisualGraph(var edges: MutableList<VisualisedEdge>, var vertices: MutableList<VisualisedVertex>)
class GraphController() {

    fun getFileNameFromDialog(): String?{
        val d = chooseFile(
            "Выбрать файл", filters = arrayOf(
                FileChooser.ExtensionFilter(
                    "Graph files",
                    "*.gr"
                )
            )
        )
        when (d.size) {
            1 -> return d.first().absolutePath
            else -> return null
        }
    }
    fun buildFromFile(fileName: String?): Graph?{
        val g = Graph()
        vertexCircle.removeFromParent()
        val inputStream: InputStream
        if (fileName == null)
            return null

        try {
            inputStream = File(fileName).inputStream()
        }
         catch(e: Exception){
             showErrorAlert("Не удается найти файл: ${fileName}")
             return null
         }

        val strs = mutableListOf<String>()
        inputStream.bufferedReader().useLines {lines -> lines.forEach {strs.add(it)}}

        val vertexList = mutableListOf<String>()
        val edgesList = mutableListOf<Triple<String, String, Int>>()

        if (strs.isNotEmpty()){
        val n_str = strs.first()

            //проверка введенного числа вершин
            if (!n_str.isInt() || n_str.toInt() < 0){
                showErrorAlert("Задано некорректное количество вершин")
                return null

            }
            val n = n_str.toInt()
            var rowsNum = 0

            //проверка на количество строк в файле(должно быть n + 2)
            if (strs.size != n + 2){
                showErrorAlert("Неверная структура файла")
                return null

            }

            //проверка на правильность введенной матрицы смежности
            val rowsElements = mutableListOf<List<String>>()
            for (i in 1 until strs.size - 1){
                rowsNum++
                rowsElements.add(strs[i].split(" "))
                for (a in rowsElements){
                    //println(a)
                }
                if (rowsElements.last().size != n){
                    showErrorAlert("Неверный размер матрицы смежности(в строке ${i} не $n элементов)")
                    return null
                }
                for (a in rowsElements.last()){
                    if (!a.isInt() || a.toInt() < 0){
                        showErrorAlert("Матрица смежности содержит недопустимые значения")
                        return null
                    }
                }
            }
            if (rowsNum !=n ){
                showErrorAlert("Неверный размер матрицы смежности, количество строк не равно $n")
                return null
            }
            for (a in strs?.last()?.split(" ")) {
                vertexList.add(a)
            }
            if (vertexList.size != n){
                showErrorAlert("Ошибка считывания имен вершин: введено неверное количество вершин")
                return null
            }
            for (v in vertexList.groupingBy{it}.eachCount()){
                if (v.value > 1){
                    showErrorAlert("Ошибка считывания имен вершин: введеные вершины содержат повторения")
                    return null
                }
            }
            for (v1 in 0 until n)
                for (v2 in 0 until n) {
                    edgesList.add(Triple(vertexList[v1], vertexList[v2], strs[1 + v1].split(" ")[v2].toInt()))
                }

            for (e in vertexList){
                g.addVertex(e)
               // println(e)
            }

            for (e in edgesList){
               // println("v1: ${e.first} v2: ${e.second} weight: ${e.third}")
                g.addEdge(e.first, e.second, e.third)
            }
            graph = g!!
            graphIsSet = true
            buildVisual()
            return graph
        }
        return null
    }
    fun randomBuild(){
        graph = Graph(GraphType.RandomGraph)
        graphIsSet = true
        vertexCircle.removeFromParent()
        buildVisual()
    }
    private fun getVisualisedGraph(): Pane{
        return Pane(painter.paintGraph(vGraph))
    }
    var state: GraphControllerState
    var graph: Graph?
    var visualEdges = mutableListOf<VisualisedEdge>()
    private var visualVertices = mutableListOf<VisualisedVertex>()
    var painter = Painter()
    var tmpVertexPair = Pair<Vertex?, Vertex?>(null, null)
    private var vGraph = VisualGraph(visualEdges, visualVertices)
    var wholePane = Pane()
    private var gPane = Pane()
    var vertexCircle = StackPane()
    var graphIsSet = false
    var justBuilt = false

    init{
        state = GraphControllerState.NOTEDITING
        graph = Graph()
        wholePane.maxWidth = 600.0
        wholePane.maxHeight = 500.0
        wholePane.prefWidth = 500.0
        wholePane.prefHeight = 500.0
        wholePane.useMaxSize = true
        vertexCircle = vertexCursor(this)
        vertexCircle.isVisible = false
        wholePane.setOnMouseEntered{

            if (state == GraphControllerState.ADDINGVERTEX){
                if (isInsidePane(it.x, it.y)){
                    if (!wholePane.children.contains(vertexCircle))
                        wholePane.add(vertexCircle)
                    vertexCircle.layoutX = it.x - 25.0
                    vertexCircle.layoutY = it.y - 25.0
                    vertexCircle.isVisible = true
                }
            }
            else vertexCircle.removeFromParent()
        }
        wholePane.setOnMouseMoved{
            if (state == GraphControllerState.ADDINGVERTEX){
                if (isInsidePane(it.x - 25.0, it.y - 25.0)){
                    if (!wholePane.children.contains(vertexCircle))
                        wholePane.add(vertexCircle)
                    vertexCircle.layoutX = it.x - 25.0
                    vertexCircle.layoutY = it.y - 25.0
                    vertexCircle.isVisible = true

                }
            }
            else vertexCircle.removeFromParent()
        }
        vertexCircle.setOnMouseClicked{
            when (state){
                GraphControllerState.ADDINGVERTEX -> {
                    addVisualVertex(vertexCircle.layoutX, vertexCircle.layoutY)
                    state = GraphControllerState.NOTEDITING
                    vertexCircle.isVisible = false
                    graphIsSet = true
                }
            }
        }
    }
    fun updateVisual(){
        vGraph = VisualGraph(visualEdges, visualVertices)
        gPane = getVisualisedGraph()
        wholePane.children.removeIf{it.id == gPane.id || it.id == vertexCircle.id}
        wholePane.add(gPane)
        if (!justBuilt) {
            wholePane.prefWidthProperty().set(gPane.boundsInLocal.width + 15)
            wholePane.prefHeightProperty().set(gPane.boundsInLocal.height + 80)

        }
        justBuilt = false
        wholePane.add(vertexCircle)
    }
    fun buildVisual(){
        justBuilt = true
        state = GraphControllerState.NOTEDITING
        visualVertices.clear()
        visualEdges.clear()
        gPane.maxWidth = 500.0
        gPane.maxHeight = 500.0
        wholePane.minWidthProperty().set(500.0)
        wholePane.minHeightProperty().set(500.0)
        wholePane.usePrefHeight
        wholePane.usePrefWidth
        val r = 180.0
        val num = graph!!.getVertices().size
        val vwc = mutableListOf<Triple<Vertex, Double, Double>>() //vertices with coordinates
        val rad = 25.0
        //vertices
        for (i in 0 until num){
            val v = graph!!.getVertices()[i]
            val cntry = 500 / 2 - 40
            val cntrx = 500 / 2 - 40
            val circle_x = r * Math.cos(2 * Math.PI * i/num) + cntrx
            val circle_y = r * Math.sin(2 * Math.PI * i/num) + cntry
            vwc.add(Triple(graph!!.getVertices()[i], circle_x + 25.0, circle_y + 25.0))
            val circle =  Circle()
            circle.setRadius(rad)
            circle.setCenterX(circle_x)
            circle.setCenterY(circle_y)
            circle.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
            val lb = Label(graph!!.getVertices()[i].getValue())
            lb.setStyle("-fx-font-smoothing-type: lcd; -fx-fill: white; -fx-font-size: 22pt;")
            val s = StackPane(circle, lb)
            s.layoutX = circle_x
            s.layoutY = circle_y
            s.maxHeight = 500.0
            s.maxWidth = 500.0
            visualVertices.add(VisualisedVertex(this, v, xy = Pair(circle_x + 25.0, circle_y + 25.0),
                nodesList = mutableListOf(s)))
        }
        //wholePane.prefHeight = gPane.prefHeight
        updateVisualEdges()

    }
    fun hightLightWithOpacity(e: VisualisedEdge){
        for (eee in visualEdges){
            if (eee.edge.first != e.edge.first || eee.edge.second != e.edge.second) {
                for (a in eee.nodesList)
                    a.opacity = 0.3
            }
        }
    }
    fun restoreOpacities(){
        for (eee in visualEdges){
                for (a in eee.nodesList)
                    a.opacity = 1.0
        }
    }
    fun restoreEdgesStyles(){

        highlightEdges(visualEdges, width = 2.0, type = "normal", color = Color.BLACK, labelColor = Color.BLACK, fontSize = 17.0)
    }
    fun isInsidePane(x: Double, y: Double): Boolean{

       if (wholePane.layoutX < x &&
            x + 100 <= wholePane.maxWidth&&
            wholePane.layoutY < y &&
            y + 50 <= wholePane.maxHeight) {
           return true
        }
       return false
    }
    fun restoreVerticesStyle(except: MutableList<VisualisedVertex> = mutableListOf<VisualisedVertex>()){

        val toColorList = mutableListOf<VisualisedVertex>()
        for (node in visualVertices)
            if (!(node in except))
                toColorList.add(node)
        if (toColorList.isNotEmpty())
            highlightVertices(toColorList, Color.BLACK, 2.0)
    }
    fun updateVisualEdges(offset: Double = 0.0){
        //edges

        visualEdges.clear()
        for (a in 0 until graph!!.getMatrix().size){
            for (b in 0 until graph!!.getMatrix()[a].size){
                val edgePane = Pane()
                if (graph!!.getMatrix()[a][b] > 0) {
                    val line = Line(visualVertices[a].xy.first + offset, visualVertices[a].xy.second + offset, visualVertices[b].xy.first + offset,
                        visualVertices[b].xy.second + offset)
                    var edge = shortenedLine(line, 25.0)
                    edge = shortenedLine(Line(edge.endX, edge.endY, edge.startX, edge.startY), 25.0)
                    edge = Line(edge.endX, edge.endY, edge.startX, edge.startY)
                    val startX = edge.startX
                    val startY = edge.startY
                    val endX = edge.endX
                    val endY = edge.endY
                    var centerX = (edge.endX + edge.startX) / 2
                    var centerY = (edge.endY + edge.startY) / 2
                    val dist = 45.0
                    if (a == b){
                        val list: List<Node> = drawLoop(startX, startY)
                        for (e in list)
                            edgePane.add(e)
                    }
                    else
                        if (graph!!.getMatrix()[b][a] > 0) {
                            val mul = if (b > a) -1 else 1
                            var curvePair = returnPoint(startX, endX, startY, endY, dist, mul)
                            centerX = curvePair.first
                            centerY = curvePair.second
                            val curve = QuadCurve(
                                startX,
                                startY,
                                centerX,
                                centerY,
                                endX,
                                endY
                            )

                            curve.fill = Color.TRANSPARENT
                            curve.stroke = Color.BLACK
                            curve.strokeWidth = 2.0
                            edgePane.add(curve)
                            edgePane.add(Arrow(curvePair.first, curvePair.second, curve.endX, curve.endY, 10.0, true))
                            curvePair = returnPoint(startX, endX, startY, endY, dist / 2, mul)
                            centerX = curvePair.first
                            centerY = curvePair.second
                        }
                        else
                            edgePane.add(Arrow(startX, startY, endX, endY, 10.0))

                    val weight = Label(graph!!.getMatrix()[a][b].toString())
                    weight.setStyle("-fx-font-smoothing-type: lcd; -fx-fill: white; -fx-font-size: 17pt;")
                    weight.layoutX = centerX + if (a == b) 25.0 * 2.2 else 0.0
                    weight.layoutY = centerY - if (a == b) 15.0 else 0.0
                    edgePane.add(weight)
                    val nodes = mutableListOf<Node>()
                    for (a in edgePane.children){
                        nodes.add(a)
                    }
                    visualEdges.add(VisualisedEdge(this,
                        Triple(visualVertices[a].vertex, visualVertices[b].vertex, graph!!.getMatrix()[a][b]),
                        nodesList = nodes, startX = startX, startY = startY, endX = endX, endY = endY))
                }
            }
        }
        updateVisual()
    }
    fun addVisualEdge(v1: Vertex, v2: Vertex, w: Int){
        graph!!.addEdge(v1, v2, w)
        updateVisualEdges()
    }
    fun getVisualEdge(v1: Vertex, v2: Vertex):VisualisedEdge?{
        val ed: VisualisedEdge? = visualEdges.find{it.edge.first.getValue() == v1.getValue() &&
            it.edge.second.getValue() == v2.getValue()}
        return ed
    }
    fun getVisualVertex(v: Vertex): VisualisedVertex?{
        val vee: VisualisedVertex? = visualVertices.find{it.vertex.getValue() == v.getValue()}
        return vee
    }
    fun addVisualVertex(x: Double, y: Double){
        state = GraphControllerState.NOTEDITING
        val name = getStringFromTextInput()
        if (name == null){
            return
        }
        for (a in graph!!.getVertices()){
            if (a.getValue() == name){
                showErrorAlert("Такая вершина уже есть")
                return
            }
        }
        graph!!.addVertex(name)
        val v = graph!!.getVertex(name)
        val vPane = painter.paintVertex(v!!, x, y, 25.0)
        val vvvv = VisualisedVertex(this, v, xy = Pair(x + 25.0, y + 25.0), nodesList = mutableListOf(vPane))
        visualVertices.add(vvvv)
        updateVisualEdges()
    }
    fun delVisualVertex(visVertex: VisualisedVertex){
        graph!!.delVertex(visVertex.vertex.getValue())
        visualVertices.removeIf{x -> x.vertex.getValue() == visVertex.vertex.getValue()}
        for (a in visVertex.nodesList)
            a.removeFromParent()
        updateVisualEdges()
    }
    fun highlightEdges(edgesToHighlight: List<VisualisedEdge>, color: Color, labelColor: Color, width: Double = 5.0, fontSize: Double = 25.0, type: String = "bold",
    highlight: Boolean = false){
        for (e in edgesToHighlight){
            e.highlighted = highlight
            for (node in e.nodesList){
                node.toFront()
                when (node){
                    is QuadCurve -> {
                        node.strokeWidth = width
                        node.stroke = color
                    }
                    is CubicCurve -> {
                        node.strokeWidth = width
                        node.stroke = color
                    }
                    is Label -> {
                        node.setStyle("-fx-font-weight: ${type}; -fx-font-smoothing-type: lcd; -fx-fill: white; -fx-font-size: ${fontSize}pt;")
                        node.textFill = labelColor
                    }
                    is Path -> {
                        node.strokeWidth = width
                        node.stroke = color
                    }
                    is Line -> {
                        node.strokeWidth = width
                        node.stroke = color
                    }
                }
            }
        }
    }
    fun highlightVertices( verticesToHighLight: List<VisualisedVertex>, color: Color,width: Double = 3.5, highlight: Boolean = false){
        for (ver in verticesToHighLight) {
            ver.highlighted = highlight
            for (cir in ver.nodesList.last()!!.getChildList()!!)
                if (cir is Circle) {
                    cir.stroke = color
                    cir.strokeWidth = width
                }
        }
    }
    fun changeLayout(vv: VisualisedVertex, x: Double, y: Double){

        for (node in vv.nodesList){
            node.layoutX = x
            node.layoutY = y
        }
        vv.xy = Pair(x+ 25, y + 25)

    }
    fun saveToFile(){

        val fileChooser = FileChooser()
        fileChooser.getExtensionFilters().add(FileChooser.ExtensionFilter(
            "Graph files",
            "*.gr"
        ))
        val f: File? = fileChooser.showSaveDialog(null)
        if (f != null && graph != null) {
            var text = "${graph!!.getVertices().size}\n"
            for (a in graph!!.getMatrix().indices) {
                text += graph!!.getMatrix()[a].joinToString(" ")
                text += "\n"
            }
            for (v in graph!!.getVertices())
                text += "${v.toString()} "
            text = text.take(text.length - 1)
            f.writeText(text)
        }
        else return
    }
}
fun vertexCursor(gController: GraphController): StackPane{

    val ve = gController.painter.paintVertex(Vertex("?", 0), 0.0, 0.0, 25.0)
    return ve
}
fun getStringFromTextInput(): String?{
    val dialog = TextInputDialog()
    dialog.headerText = ""
    dialog.contentText = "Введите имя вершины"
    val string = dialog.showAndWait()
    if (string.isPresent && !string.get().isEmpty())
        return string.get()
    else return null
}
fun showErrorAlert(str: String){
    val a =  Alert(Alert.AlertType.ERROR, str,)
    a.title = "Ошибка"
    a.headerText = ""
    a.show()
}
fun showInfoAlert(str: String){
    val a =  Alert(Alert.AlertType.INFORMATION, str,)
    a.title = "Сообщение"
    a.headerText = ""
    a.show()
}