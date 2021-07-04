package com.example.graph

import kotlin.random.Random

enum class GraphType {
    RandomGraph, CommonGraph
}

class Graph(graphType: GraphType = GraphType.CommonGraph) {
    private var edges: ArrayList<ArrayList<Int>> = ArrayList<ArrayList<Int>>()
    private var vertices: ArrayList<Vertex> = ArrayList<Vertex>()


    init {
        if (graphType == GraphType.RandomGraph) {
            //generate random graph
            val numberOfVertices: Int = Random.nextInt(4, 15)
            val numberOfEdges: Int = Random.nextInt(numberOfVertices - 1, numberOfVertices * (numberOfVertices - 1))
            for (i in 1..numberOfVertices) {
                addVertex(i.toString())
            }
            for (i in 0..numberOfVertices - 1) {
                for (j in 0..numberOfVertices - 1) {
                    addEdge(vertices[i], vertices[j], Random.nextInt(1, 50))
                }
            }
            var k = numberOfVertices * (numberOfVertices - 1)
            while (k != numberOfEdges) {
                while (!delEdge(
                        vertices[Random.nextInt(0, numberOfVertices - 1)],
                        vertices[Random.nextInt(0, numberOfVertices - 1)]
                    )
                ) {
                }
                k -= 1
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