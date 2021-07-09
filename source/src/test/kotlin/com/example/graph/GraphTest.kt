package com.example.graph

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.runners.Parameterized
import tornadofx.isInt
import java.io.File
import java.io.InputStream
import kotlin.test.assertEquals

internal class GraphTest() {
    var data: String? = null

    @DisplayName("GraphTest")
    @ParameterizedTest
    @MethodSource("graphs")
    fun test(path: String) {
        var gr = copyBuildFromFile("src/test/kotlin/tests/Graph/" + path)
        if (gr == null) {
            assertEquals(1, 2)
            return
        }
        val matrix = gr.getMatrix()
        val vertexes = gr.getVertices()
        val inputStream: InputStream
        inputStream = File("src/test/kotlin/tests/Graph/" + path).inputStream()
        val strs = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { strs.add(it) } }
        val size = strs.first()
        assertEquals(size, matrix.size.toString())
        val rowsElements = mutableListOf<List<String>>()
        for (i in 1 until strs.size - 1) {
            rowsElements.add(strs[i].split(" "))
            for (a in rowsElements.indices) {
                for (b in rowsElements.indices) {
                    assertEquals(
                        rowsElements[a][b].toInt(), matrix.get(a).get(b)
                    )
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun graphs(): Collection<Array<Any>> {
            return listOf(
                arrayOf("test1.gr"),         // First test:  (paramOne = 1, paramTwo = "I")
                arrayOf("test2.gr"), // Second test: (paramOne = 1999, paramTwo = "MCMXCIX")
                arrayOf("test3.gr"), // Second test: (paramOne = 1999, paramTwo = "MCMXCIX")
                arrayOf("test4.gr"),// Second test: (paramOne = 1999, paramTwo = "MCMXCIX")
                arrayOf("test5.gr"), // Second test: (paramOne = 1999, paramTwo = "MCMXCIX")
                arrayOf("test6.gr") // Second test: (paramOne = 1999, paramTwo = "MCMXCIX")
            )
        }
    }

    fun copyBuildFromFile(fileName: String?): Graph? {
        var g = Graph()
        val inputStream: InputStream
        if (fileName == null)
            return null

        try {
            inputStream = File(fileName).inputStream()
        } catch (e: Exception) {
            return null
        }

        val strs = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines -> lines.forEach { strs.add(it) } }

        var vertexList = mutableListOf<String>()
        var edgesList = mutableListOf<Triple<String, String, Int>>()

        if (!strs.isEmpty()) {
            var n_str = strs.first()

            //проверка введенного числа вершин
            if (!n_str.isInt() || n_str.toInt() < 0) {
                return null
            }
            val n = n_str.toInt()
            var rowsNum = 0

            //проверка на количество строк в файле(должно быть n + 2)
            if (strs.size != n + 2) {
                return null
            }

            //проверка на правильность введенной матрицы смежности
            val rowsElements = mutableListOf<List<String>>()
            for (i in 1 until strs.size - 1) {
                rowsNum++
                rowsElements.add(strs[i].split(" "))
                for (a in rowsElements) {
                    //println(a)
                }
                if (rowsElements.last().size != n) {
                    return null
                }
                for (a in rowsElements.last()) {
                    if (!a.isInt() || a.toInt() < 0) {
                        return null
                    }
                }
            }
            if (rowsNum != n) {
                return null
            }
            for (a in strs?.last()?.split(" ")) {
                vertexList.add(a)
            }
            if (vertexList.size != n) {
                return null
            }
            for (v in vertexList.groupingBy { it }.eachCount()) {
                if (v.value > 1) {
                    return null
                }
            }
            for (v1 in 0 until n)
                for (v2 in 0 until n) {
                    edgesList.add(Triple(vertexList[v1], vertexList[v2], strs[1 + v1].split(" ")[v2].toInt()))
                }

            for (e in vertexList) {
                g.addVertex(e)
                // println(e)
            }

            for (e in edgesList) {
                // println("v1: ${e.first} v2: ${e.second} weight: ${e.third}")
                g.addEdge(e.first, e.second, e.third)
            }

            return g
        }
        return null
    }

}