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

    fun recursiveGetPath(vertex: Vertex): String {
        val currIndex = vertex.getIndex()
        var path: String = ""
        var curr: Pair<Vertex?, Int>? = null

        for (i in 0..table.size - 1) {
            if (table[i][currIndex].first != null) {
                curr = table[i][currIndex]
                if (i == 0) {
                    return curr.first?.getValue() ?: ""
                }
            }
        }
        if (curr == null) {//нет числа, значит нет пути
            return ""
        }
        if (curr.first == null) {//Если последний Inf, значит нет пути
            return ""
        } else {
            return recursiveGetPath(curr.first!!) + "->" + vertex.getValue()//путь есть переходим к предыдущему, пока не дойдем до 0 строчки
        }
    }

    fun getPath(vertex: Vertex): String {
        val currIndex = vertex.getIndex()
        var curr: Pair<Vertex?, Int>? = null
        for (i in 0..table.size - 1) {
            if (table[i][currIndex].first != null) {
                curr = table[i][currIndex]
            }
        }
        if (curr == null) {
            return "="
        }
        return recursiveGetPath(vertex) + "=" + (curr.second)
    }

    fun getQueue(): PriorityQueue<Pair<Vertex, Int>> {
        return queue
    }

    //потом удалю
    fun getLine(
        indexesOfInf: ArrayList<Int>,
        indexesOfNull: ArrayList<Int>,
        values: ArrayList<Pair<Vertex, Int>>
    ): ArrayList<Pair<Vertex?, Int>> {
        var curr = ArrayList<Pair<Vertex?, Int>>()
        val prevLine = table[table.size - 1]
        for (i in 0..prevLine.size - 1) {
            //Если на предыдущей нулл, всегда нулл
            if (i in indexesOfNull || prevLine[i].first == null && prevLine[i].second == -1) {
                curr.add(Pair(null, -1))
                continue
            }
            if (values[i].first.getValue() == "") {
                curr.add(prevLine[i])
                continue
            } else {
                curr.add(values[i])
                continue
            }
        }
        return curr
    }

    fun getTable(): ArrayList<ArrayList<Pair<Vertex?, Int>>> {
        return table
    }

    fun getState(): DijkstraState {
        return dijkstraState
    }

}