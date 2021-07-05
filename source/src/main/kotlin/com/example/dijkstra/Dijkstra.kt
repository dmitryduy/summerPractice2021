package com.example.dijkstra

import com.example.graph.Graph
import com.example.graph.Vertex
import java.util.*
import kotlin.collections.ArrayList

class MyComparator : Comparator<Pair<Vertex, Int>> {
    override fun compare(o1: Pair<Vertex, Int>?, o2: Pair<Vertex, Int>?): Int {
        return Integer.compare(o1?.second ?: 0, o2?.second ?: 0)
    }
}

class Dijkstra {
    private fun changeElemInQueue(queue: PriorityQueue<Pair<Vertex, Int>>, vertex: Vertex, newInt: Int) {
        for (i in queue) {
            if (i.first.getValue() == vertex.getValue()) {
                val new = Pair<Vertex, Int>(i.first, newInt);
                queue.remove(i)
                queue.add(new)
                return
            }
        }
    }

    private fun findElemInQueue(index: Int, queue: PriorityQueue<Pair<Vertex, Int>>): Pair<Vertex, Int>? {
        for (i in queue) {
            if (i.first.getIndex() == index) {
                return i
            }
        }
        return null
    }

    fun makeAlgorithm(graph: Graph, start: Vertex) {
        //init queue
        val queue: PriorityQueue<Pair<Vertex, Int>> = PriorityQueue<Pair<Vertex, Int>>(MyComparator())
        val result: ArrayList<Pair<Vertex, Int>> = ArrayList<Pair<Vertex, Int>>()
        for (i in graph.getVertices()) {
            queue.add(Pair<Vertex, Int>(i, Integer.MAX_VALUE))
        }
        val marked = Array<Int>(graph.getVertices().size) { 0 }//массив помеченных вершин
        changeElemInQueue(queue, start, 0)
        // var a = queue.poll()- удаляет и дает минимальный
        for (i in 0..graph.getVertices().size - 1) {
            var curr = queue.poll()
            if (curr == null) break
            for (j in 0..graph.getVertices().size - 1) {
                if (curr.first.getIndex() == j) continue
                if (graph.getMatrix()[curr.first.getIndex()][j] > 0) {
                    val secondVertex = findElemInQueue(j, queue)
                    if (secondVertex != null) {
                        if (secondVertex.second > curr.second + graph.getMatrix()[curr.first.getIndex()][j]) {
                            changeElemInQueue(
                                queue,
                                secondVertex.first,
                                curr.second + graph.getMatrix()[curr.first.getIndex()][j]
                            )
                        }
                    }
                }
            }
            result.add(curr)
        }
        println( result )
    }
}