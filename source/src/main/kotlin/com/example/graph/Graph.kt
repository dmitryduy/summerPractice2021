package com.example.graph

import com.example.dijkstra.DijkstraSteps
import org.junit.jupiter.api.Assertions
import kotlin.random.Random

enum class GraphType {
    RandomGraph, CommonGraph
}

class Graph(graphType: GraphType = GraphType.CommonGraph) {
    private var edges: ArrayList<ArrayList<Int>> = ArrayList<ArrayList<Int>>()
    private var vertices: ArrayList<Vertex> = ArrayList<Vertex>()

    init {
        /*
        if (graphType == GraphType.RandomGraph) {
            val string = "0 4 0 0 0 0 0 0 0 0 0 0 " +//A
                    "0 0 4 0 0 7 0 0 0 0 0 0 " +//B
                    "0 0 0 0 0 0 7 0 0 0 0 0 " +//C
                    "0 0 3 0 0 0 1 6 0 0 0 0 " +//D
                    "7 0 0 0 0 0 0 0 4 0 0 0 " +//E
                    "0 0 6 0 4 0 0 0 0 10 0 0 " +//F
                    "0 0 0 1 0 8 0 9 0 9 7 6 " +//G
                    "0 0 0 0 0 0 9 0 0 0 0 5 " +//H
                    "0 0 0 0 4 4 0 0 0 9 0 0 " +//I
                    "0 0 0 0 0 10 9 0 9 0 0 0 " +//J
                    "0 0 0 0 0 0 0 0 0 0 0 0 " +//K
                    "0 0 0 0 0 0 6 0 0 0 10 0" //L
            //A+ B+ C+ D+ E+ F+ G+ H+ I+ J K L
            val edges = string.split(" ")
            val stringVer: String = "A B C D E F G H I J K L"

            val vertices = stringVer.split(" ")

            for (i in vertices) {
                addVertex(i)
            }
            for (i in 0..vertices.size - 1) {
                for (j in 0..vertices.size - 1) {
                    addEdge(vertices[i], vertices[j], edges[i * vertices.size + j].toInt())
                }
            }
        }*/
        //generate random graph
        //  val numberOfVertices: Int = Random.nextInt(4, 10)
        if (graphType == GraphType.RandomGraph) {
            val numberOfVertices: Int = Random.nextInt(4, 10)
            //val numberOfEdges: Int = Random.nextInt(numberOfVertices - 1, numberOfVertices * (numberOfVertices - 1))
            for (i in 1..numberOfVertices) {
                addVertex(('A' + i - 1).toString())
            }
            for (i in 0..numberOfVertices - 1) {
                var k = 0
                if (numberOfVertices > 6) {
                    k = Random.nextInt(2, numberOfVertices - 4) / 2
                } else {
                    k = Random.nextInt(2, numberOfVertices - 1)
                }
                for (j in 0..k) {
                    var n = Random.nextInt(vertices.size)
                    if (i != n) {
                        addEdge(vertices[i], vertices[n], Random.nextInt(1, 6))
                    } else {
                        n = Random.nextInt(vertices.size)
                        addEdge(vertices[i], vertices[n], Random.nextInt(1, 6))
                    }
                }
            }
        }

    }

    fun getMatrix(): ArrayList<ArrayList<Int>> {
        return edges
    }

    fun getVertices(): ArrayList<Vertex> {
        return vertices
    }

    fun addEdge(first: String, second: String, weight: Int): Boolean {
        val _first = getVertex(first)
        val _second = getVertex(second)
        if (_first != null && _second != null) {
            edges[_first.getIndex()][_second.getIndex()] = weight
        }
        if (_first != null && _second == null) {
            edges[_first.getIndex()][addVertex(second).getIndex()] = weight
            return true
        }
        if (_first != null && _second != null) {
            edges[addVertex(first).getIndex()][_second.getIndex()] = weight
            return true
        }
        edges[addVertex(first).getIndex()][addVertex(second).getIndex()] = weight
        return true
    }

    fun addEdge(first: Vertex, second: Vertex, weight: Int): Boolean {
        return addEdge(first.toString(), second.toString(), weight)
    }

    fun delEdge(first: String, second: String): Boolean {
        val _first = getVertex(first)
        val _second = getVertex(second)
        if (_first != null && _second != null && edges[_first.getIndex()][_second.getIndex()] != 0) {
            edges[_first.getIndex()][_second.getIndex()] = 0
            return true
        }
        return false
    }

    fun delEdge(first: Vertex, second: Vertex): Boolean {
        return delEdge(first.getValue(), second.getValue())
    }

    fun addVertex(value: String): Vertex {
        for (i in vertices) {
            if (i.getValue() == value) {
                return i
            }
        }
        vertices.add(Vertex(value, vertices.size))
        val newLine = ArrayList<Int>()
        for (i in 1..edges.size) {
            newLine.add(0)
        }
        edges.add(newLine)
        for (i in edges) {
            i.add(0)
        }
        return vertices[vertices.size - 1]
    }

    fun delVertex(value: String) {
        val ver = getVertex(value)
        if (ver != null) {
            for (i in (ver.getIndex() + 1)..(vertices.size - 1)) {
                vertices[i].setIndex(vertices[i].getIndex() - 1)
            }
            vertices.removeAt(ver.getIndex())
            edges.removeAt(ver.getIndex())
            for (i in edges) {
                i.removeAt(ver.getIndex())
            }
        }
    }

    fun delVertex(vertex: Vertex) {
        delVertex(vertex.getValue())
    }

    fun getVertex(value: String): Vertex? {
        for (i in vertices) {
            if (i.getValue() == value) {
                return i
            }
        }
        return null
    }
}