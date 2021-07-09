package com.example.painter
import com.example.visualised.*
import com.example.graph.*
import com.example.graphcontroller.*
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.*
import tornadofx.*
import javafx.scene.shape.QuadCurveTo;

class Arrow @JvmOverloads constructor(
    startX: Double,
    startY: Double,
    endX: Double,
    endY: Double,
    arrowHeadSize: Double = defaultArrowHeadSize,
    curved: Boolean = false
) :
    Path() {
    companion object {
        private const val defaultArrowHeadSize = 5.0
    }

    init {
        //strokeProperty().bind(fillProperty())
        setFill(Color.BLACK)
        setStrokeWidth(2.0)

        //Line
        getElements().add(MoveTo(startX, startY))

        if (curved == false)
            getElements().add(LineTo(endX, endY))
        else getElements().add(MoveTo(endX, endY))

        //ArrowHead
        val angle = Math.atan2(endY - startY, endX - startX) - Math.PI / 2.0
        val sin = Math.sin(angle)
        val cos = Math.cos(angle)
        //point1
        val x1 = (-1.0 / 2.0 * cos + Math.sqrt(3.0) / 2 * sin) * arrowHeadSize + endX
        val y1 = (-1.0 / 2.0 * sin - Math.sqrt(3.0) / 2 * cos) * arrowHeadSize + endY
        //point2
        val x2 = (1.0 / 2.0 * cos + Math.sqrt(3.0) / 2 * sin) * arrowHeadSize + endX
        val y2 = (1.0 / 2.0 * sin - Math.sqrt(3.0) / 2 * cos) * arrowHeadSize + endY
        getElements().add(LineTo(x1, y1))
        getElements().add(LineTo(x2, y2))
        getElements().add(LineTo(endX, endY))

    }
}

fun shortenedLine(line : Line, len: Double): Line{
    val x2 = line.endX
    val x1 = line.startX
    val y2 = line.endY
    val y1 = line.startY

    var dx: Double = x2 - x1
    var dy: Double = y2 - y1
    val length: Double = Math.sqrt(dx * dx + dy * dy)
    if (length > 0) {
        dx /= length
        dy /= length
    }
    dx *= length - len
    dy *= length - len
    val x3 = (x1 + dx)
    val y3 = (y1 + dy)
    return Line(x1, y1, x3, y3)

}
fun returnPoint(x1: Double, x2: Double, y1: Double, y2: Double, distance: Double, minus: Int): Pair<Double, Double>{
    if (Math.abs(y2 - y1) < 1)
        return Pair((x1 + x2)/2, y1 + distance * minus)
    var res = y2 - y1
    val k = (x1 - x2)/res
    //println(k)
    val b = (y1 + y2)/2 - (x1 + x2)/2 * k
    val x1 = (x1 + x2) / 2
    val y1 = (y1 + y2) / 2
    var x3 = x1 + distance/Math.sqrt(1 + k*k) * minus
    var y3 = k * x3 + b
    return Pair(x3, y3)
}
fun drawLoop(x: Double, y: Double, angle: Double = 0.0): List<Node>{
    var nodesList =  mutableListOf<Node>()
    val xpar = 30
    val ypar = 15
    var x = x + 25.0
    nodesList.add(Line(x, y, x + xpar, y - ypar))
    nodesList.add(Line(x, y, x + xpar, y + ypar))

    var cc = CubicCurve(x + xpar, y + ypar, x + 2*xpar, y + ypar + 10, x + 2 *xpar, y - ypar - 10, x + xpar, y - ypar)
    cc.fill = Color.TRANSPARENT
    cc.stroke = Color.BLACK
    cc.strokeWidth = 2.0
    nodesList.add(cc)
    nodesList.add(Arrow(x + xpar, y + ypar, x, y, 10.0, true))
    for (line in nodesList)
        if (line is Line)
            line.strokeWidth = 2.0
    return nodesList
}
fun drawCurvedEdge(edge: VisualisedEdge, curveSide: Boolean): VisualisedEdge{

    var edgePane = Pane()
    val dist = 45.0
    val startX = edge.startX
    val startY = edge.startY
    val endX = edge.endX
    val endY = edge.endY
    val mul = if (curveSide) -1 else 1
    var curvePair = returnPoint(startX, endX, startY, endY, dist, mul)
    val centerX = curvePair.first
    val centerY = curvePair.second
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
    edgePane.add(curve)
    edgePane.add(Arrow(curvePair.first, curvePair.second, curve.endX, curve.endY, 10.0, true))
    //curvePair = returnPoint(startX, endX, startY, endY, dist / 2, mul)
    //centerX = curvePair.first
    //centerY = curvePair.second
    val pointOfLabel = returnPoint(startX, endX, startY, endY, dist / 2, mul)
    var lb = Label(edge.edge.third.toString())
    lb.layoutX = pointOfLabel.first
    lb.layoutY = pointOfLabel.second
    edgePane.add(lb)
    edge.nodesList.clear()
    for (a in edgePane.children)
        edge.nodesList.add(a)
    return edge
}
class Painter{
    fun paintVertex(v: Vertex, x: Double, y: Double, rad: Double = 25.0): StackPane {

        var circle =  Circle()
        circle.setRadius(rad)
        circle.setCenterX(x)
        circle.setCenterY(y)
        circle.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        var lb = Label(v.getValue())
        lb.setStyle("-fx-font-smoothing-type: lcd; -fx-fill: white; -fx-font-size: 22pt;")
        var s = StackPane(circle, lb)
        s.layoutX = x
        s.layoutY = y
        s.prefHeight = 30.0
        s.prefWidth = 30.0
        return s
    }
    fun paintGraph(vGraph: VisualGraph): Pane{
        var p = Pane()
        p.maxHeight = 500.0
        p.maxWidth = 500.0
        for (vv in vGraph.vertices){
            for (node in vv.nodesList)
                p.add(node)
        }
        for (ee in vGraph.edges)
            for (node in ee.nodesList)
                p.add(node)

        return p
    }
}
