package com.example.graphcontroller

import com.example.graph.*
import javafx.scene.control.Alert
import javafx.stage.FileChooser
import tornadofx.chooseFile
import tornadofx.isInt
import java.io.File

class GraphController {
    fun buildFromFile(): Graph? {
        var g = Graph()
        var d = chooseFile(
            "Выбрать файл", filters = arrayOf(
                FileChooser.ExtensionFilter(
                    "Graph files",
                    "*.gr"
                )
            )
        )
        val graphFile: File? = when (d.size) {
            1 -> d.first()
            else -> null
        }
        var vertexList = mutableListOf<String>()
        var edgesList = mutableListOf<Triple<String, String, Int>>()

        if (graphFile != null) {
            val strs: List<String> = graphFile!!.readLines()

            var n_str = strs.first()

            //проверка введенного числа вершин
            if (!n_str.isInt() || n_str.toInt() < 0){
                showErrorAlert("Задано некорректное количество вершин",)
                return null
            }
            val n = n_str.toInt()
            var rowsNum = 0

            //проверка на количество строк в файле(должно быть n - 2)
            if (strs.size != n + 2){
                showErrorAlert("Неверная структура файла",)
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
                showErrorAlert("Неверный размер матрицы смежности, количество строк не равно $n",)
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
            return g
        }
        return null
    }
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