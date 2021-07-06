package com.example.dijkstra

import com.example.graph.Vertex
import java.util.*
import kotlin.collections.ArrayList

enum class DijkstraState {
    Start,//ничего не происходит
    VertexProcessing, //обработка вершины,на этом шаге подсвечиваются вершины
    UpdatedPath, //обновлены пути
    UpdatedQueue, //Изменилась очередь
    UpdatedTable //добавилась строка в таблицу
}

class DijkstraStep(
    private var dijkstraState: DijkstraState,
    private var queue: PriorityQueue<Pair<Vertex, Int>>,
    private val table: ArrayList<ArrayList<Pair<Vertex?, Int>>>,//вершина откуда вернулся и расстояние
    private val currVertex: Vertex//таблица строки inf или окуда вернулся и текущее расстояние до вершины
) : Cloneable {
    public override fun clone(): Any {
        return super.clone()
    }

    fun setQueue(_queue: PriorityQueue<Pair<Vertex, Int>>) {
        queue = _queue
    }

    fun setState(state: DijkstraState) {
        dijkstraState = state
    }

    fun getCurrVertex(): Vertex {
        return currVertex
    }

    fun getPaths(): ArrayList<String> {
        val paths = ArrayList<String>()
        paths.add("D→G=1")
        paths.add("D→C=3")
        paths.add("D→H=6")
        paths.add("D→G→L=7")
        paths.add("D→G→K=8")
        paths.add("D→G→F=9")
        paths.add("D→G→J=10")
        paths.add("D→G→F→E=13")
        paths.add("D→G→F→E→I=17")
        paths.add("D→G→F→E→A=20")
        paths.add("D→G→F→E→A→B=24")
        return paths
    }

    fun getQueue(): PriorityQueue<Pair<Vertex, Int>> {
        return queue
    }

    //потом удалю
    fun getLine(
        indexesOfInf: Array<Int>,
        indexesOfNull: Array<Int>,
        values: Array<Pair<Vertex, Int>>
    ): ArrayList<Pair<Vertex?, Int>> {
        var curr = ArrayList<Pair<Vertex?, Int>>()
        for (i in 0..11) {
            if (i in indexesOfNull) {
                curr.add(Pair(null, -1))
                continue
            }
            if (i in indexesOfInf) {
                curr.add(Pair(null, Integer.MAX_VALUE))
                continue
            }
            curr.add(values[i])
        }
        return curr
    }

    fun getTable(): ArrayList<ArrayList<Pair<Vertex?, Int>>> {
        var temp = ArrayList<ArrayList<Pair<Vertex?, Int>>>()
        var vertex = arrayOf(
            Pair(Vertex("E", 0), 20),
            Pair(Vertex("A", 0), 24),
            Pair(Vertex("D", 0), 3),
            Pair(Vertex("D", 0), 0),
            Pair(Vertex("F", 0), 13),
            Pair(Vertex("G", 0), 9),
            Pair(Vertex("D", 0), 1),
            Pair(Vertex("D", 0), 6),
            Pair(Vertex("E", 0), 17),
            Pair(Vertex("G", 0), 10),
            Pair(Vertex("G", 0), 8),
            Pair(Vertex("G", 0), 7)
        )
        //0
        temp.add(getLine(arrayOf(0, 1, 2, 4, 5, 6, 7, 8, 9, 10, 11), arrayOf(-1), vertex))
        //1
        temp.add(getLine(arrayOf(0, 1, 4, 5, 8, 9, 10, 11), arrayOf(-1), vertex))
        //2
        temp.add(getLine(arrayOf(0, 1, 4, 8), arrayOf(3), vertex))
        //3
        temp.add(getLine(arrayOf(0, 1, 4, 8), arrayOf(3, 6), vertex))
        //4
        temp.add(getLine(arrayOf(0, 1, 4, 8), arrayOf(2, 3, 6), vertex))
        //5
        temp.add(getLine(arrayOf(0, 1, 4, 8), arrayOf(2, 3, 6, 7), vertex))
        //6
        temp.add(getLine(arrayOf(0, 1, 4, 8), arrayOf(2, 3, 6, 7, 11), vertex))
        //7
        temp.add(getLine(arrayOf(0, 1, 8), arrayOf(2, 3, 6, 7, 10, 11), vertex))
        //8
        temp.add(getLine(arrayOf(0, 1), arrayOf(2, 3, 5, 6, 7, 10, 11), vertex))
        //9
        temp.add(getLine(arrayOf(1), arrayOf(2, 3, 5, 6, 7, 9, 10, 11), vertex))
        //10
        temp.add(getLine(arrayOf(1), arrayOf(2, 3, 4, 5, 6, 7, 9, 10, 11), vertex))
        //11
        temp.add(getLine(arrayOf(), arrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11), vertex))
        //12
        temp.add(getLine(arrayOf(), arrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), vertex))
        return temp
    }

    fun getState(): DijkstraState {
        return dijkstraState
    }

}