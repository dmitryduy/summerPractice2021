package com.example.graph

import com.example.dijkstra.Dijkstra
import com.example.dijkstra.DijkstraSteps
import com.example.dijkstra.MyComparatorPaths
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.runners.Parameterized
import tornadofx.isInt
import tornadofx.stringProperty
import java.io.File
import java.io.InputStream
import kotlin.test.assertEquals

internal class DijkstraTest {
    private val arrayPaths: ArrayList<String> = ArrayList<String>()
    private fun getPaths(dijkstraSteps: DijkstraSteps, vertexes: ArrayList<Vertex>): ArrayList<String> {
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

    @DisplayName("DijkstraTest")
    @ParameterizedTest
    @MethodSource("graphsDijkstra")
    fun test(path: String, expectedPaths: Array<String>) {
        val gr = copyBuildFromFile("src/test/kotlin/tests/Graph/" + path)
        val a = Dijkstra()
        var steps: DijkstraSteps?
        if (gr != null) {
            steps = a.makeAlgorithm(gr, Vertex("A", 0))
            val paths = getPaths(steps, gr.getVertices())
            assertEquals(expectedPaths.size, paths.size)
            for (i in 0..expectedPaths.size-1) {
                assertEquals(expectedPaths[i], paths[i])
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun graphsDijkstra(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    "test3.gr", arrayOf("A=0", "A->B=1", "A->C=1", "A->D=1", "A->E=1")
                ),
                arrayOf(
                    "test1.gr",
                    arrayOf(
                        "A=0",
                        "A->B=4",
                        "A->B->C=8",
                        "A->B->F=11",
                        "A->B->F->E=15",
                        "A->B->C->G=15",
                        "A->B->C->G->D=16",
                        "A->B->F->E->I=19",
                        "A->B->F->J=21",
                        "A->B->C->G->L=21",
                        "A->B->C->G->D->H=22",
                        "A->B->C->G->K=22"
                    )
                ),
                arrayOf(
                    "test2.gr", arrayOf("A=0")
                ),
                arrayOf(
                    "test4.gr", arrayOf("A=0", "A->B=1")
                ),
                arrayOf(
                    "test5.gr", arrayOf("A=0")
                ),
                arrayOf(
                    "test6.gr", arrayOf("A=0", "A->B=1", "A->D=1", "A->B->C=2", "A->B->E=2")
                )
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
            for (a in strs.last().split(" ")) {
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