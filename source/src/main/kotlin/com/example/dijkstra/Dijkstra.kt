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

class MyComparatorPaths : Comparator<String> {
    override fun compare(o1: String, o2: String): Int {
        val first = o1.substring(o1.indexOf('=') + 1).toInt()
        val second = o2.substring(o2.indexOf('=') + 1).toInt()
        return Integer.compare(first, second)
    }
}

class Dijkstra {
    private val dijkstraSteps: DijkstraSteps = DijkstraSteps()
    private lateinit var currGraph: Graph

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

    private fun initDijkstraSteps(queue: PriorityQueue<Pair<Vertex, Int>>) {
        val temp = ArrayList<Pair<Vertex?, Int>>()
        for (i in 0..queue.size - 1) {
            if (i == queue.peek().first.getIndex()) {
                temp.add(Pair(queue.peek().first, 0))
            } else {
                temp.add(Pair(null, Integer.MAX_VALUE))
            }
        }
        val res = ArrayList<ArrayList<Pair<Vertex?, Int>>>()
        res.add(temp)
        val queueNew = queue.toList()
        val queueRes: PriorityQueue<Pair<Vertex, Int>> = PriorityQueue<Pair<Vertex, Int>>(MyComparator())
        for (i in queueNew) {
            queueRes.add(Pair<Vertex, Int>(i.first, i.second))
        }
        dijkstraSteps.addStep(
            DijkstraStep(
                dijkstraState = DijkstraState.Start,
                queueRes,
                res,
                queue.peek().first,
                currGraph
            )
        )
    }

    private fun addStepVertexProcessing(vertex: Vertex) {
        val new: DijkstraStep = dijkstraSteps.next().clone() as DijkstraStep
        //val ver =Vertex( new.getCurrVertex().getValue(),new.        )
        dijkstraSteps.addStep(
            DijkstraStep(
                DijkstraState.VertexProcessing,
                new.getQueue(),
                new.getTable(),
                vertex, currGraph
            )
        )
    }

    private fun addStepUpdatedPath() {
        val new: DijkstraStep = dijkstraSteps.next().clone() as DijkstraStep
        dijkstraSteps.addStep(
            DijkstraStep(
                DijkstraState.UpdatedPath, new.getQueue(),
                new.getTable(),
                new.getCurrVertex(), currGraph
            )
        )

    }

    private fun addStepUpdatedQueue(queue: PriorityQueue<Pair<Vertex, Int>>) {
        val new: DijkstraStep = dijkstraSteps.next().clone() as DijkstraStep
        val queueNew = queue.toList()
        val queueRes: PriorityQueue<Pair<Vertex, Int>> = PriorityQueue<Pair<Vertex, Int>>(MyComparator())
        for (i in queueNew) {
            queueRes.add(Pair(i.first, i.second))
        }
        dijkstraSteps.addStep(
            DijkstraStep(
                DijkstraState.UpdatedQueue,
                queueRes,
                new.getTable(),
                new.getCurrVertex(),
                currGraph
            )
        )
    }

    private fun addStepUpdatedTable(
        indexesOfInf: ArrayList<Int>,
        indexesOfNull: ArrayList<Int>,
        values: ArrayList<Pair<Vertex, Int>>
    ) {
        val new: DijkstraStep = dijkstraSteps.next().clone() as DijkstraStep
        val prevTable = new.getTable().clone() as ArrayList<ArrayList<Pair<Vertex?, Int>>>
        val newLine = new.getLine(
            indexesOfInf, indexesOfNull,
            values
        )
        prevTable.add(newLine)
        dijkstraSteps.addStep(
            DijkstraStep(
                DijkstraState.UpdatedTable,
                new.getQueue(),
                prevTable,
                new.getCurrVertex(), currGraph
            )
        )
    }

    fun makeAlgorithm(graph: Graph, start: Vertex): DijkstraSteps {
        currGraph = graph
        dijkstraSteps.clear()
        //init queue
        val queue: PriorityQueue<Pair<Vertex, Int>> = PriorityQueue<Pair<Vertex, Int>>(MyComparator())
        val result: ArrayList<Pair<Vertex, Int>> = ArrayList<Pair<Vertex, Int>>()
        for (i in graph.getVertices()) {
            queue.add(Pair<Vertex, Int>(i, Integer.MAX_VALUE))
        }
        changeElemInQueue(queue, start, 0)
        initDijkstraSteps(queue)
        //addStepUpdatedQueue(queue)
        for (i in 0..graph.getVertices().size - 1) {
            addStepUpdatedQueue(queue)
            var curr = queue.poll()
            addStepVertexProcessing(curr.first)
            if (curr == null) break
            var indexesOfInf = ArrayList<Int>()
            var indexesOfNull = ArrayList<Int>()
            var values = ArrayList<Pair<Vertex, Int>>()
            for (j in 0..graph.getVertices().size - 1) {
                if (curr.first.getIndex() == j) {
                    indexesOfNull.add(j)
                    values.add(Pair(Vertex("", -1), -1))
                    continue
                }
                if (graph.getMatrix()[curr.first.getIndex()][j] > 0) {
                    val secondVertex = findElemInQueue(j, queue)
                    if (secondVertex != null) {
                        if (secondVertex.second > curr.second + graph.getMatrix()[curr.first.getIndex()][j]) {
                            if (curr.second != Integer.MAX_VALUE) {

                                values.add(Pair(curr.first, curr.second + graph.getMatrix()[curr.first.getIndex()][j]))
                                changeElemInQueue(
                                    queue,
                                    secondVertex.first,
                                    curr.second + graph.getMatrix()[curr.first.getIndex()][j]
                                )
                            } else {
                                values.add(Pair(Vertex("", -1), -1))
                            }
                        } else {
                            values.add(Pair(Vertex("", -1), -1))
                        }
                    } else {
                        //null должен быть
                        indexesOfNull.add(j)
                        values.add(Pair(Vertex("", -1), -1))
                    }

                } else {
                    //оставить без изменения ("",-1),-1
                    values.add(Pair(Vertex("", -1), -1))
                    indexesOfInf.add(j)
//                    newLine.add(dijkstraSteps.dijkstraSteps[dijkstraSteps.dijkstraSteps.size - 1].getTable()[dijkstraSteps.dijkstraSteps[dijkstraSteps.dijkstraSteps.size - 1].getTable().size - 1][j])
                }
            }
            addStepUpdatedTable(indexesOfInf, indexesOfNull, values)
            addStepUpdatedPath()
            result.add(curr)
        }
        return dijkstraSteps
    }
}