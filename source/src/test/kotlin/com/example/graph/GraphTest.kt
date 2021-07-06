package com.example.graph

import org.junit.Rule
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest

internal class GraphTest {
    var graph: Graph = Graph(GraphType.CommonGraph)

    @Test
    fun testInit() {
        /*  val string = "0 4 0 0 0 0 0 0 0 0 0 0 " +//A
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
            }*/
        val string = "0 5 0 3 10 " +
                "0 0 4 0 0 " +
                "0 0 0 0 0 " +
                "0 4 1 0 0 " +
                "0 0 0 2 0"
        val edges = string.split(" ")
        val stringVer: String = "A B C D E"

        val vertices = stringVer.split(" ")

        for (i in vertices) {
            graph.addVertex(i)
        }
        assertEquals("A", graph.getVertices()[0].getValue(), "A != graph.vertices[0]")
        assertEquals("B", graph.getVertices()[1].getValue(), "B != graph.vertices[0]")
        assertEquals("C", graph.getVertices()[2].getValue(), "C != graph.vertices[0]")
        assertEquals("D", graph.getVertices()[3].getValue(), "D != graph.vertices[0]")
        assertEquals("E", graph.getVertices()[4].getValue(), "E != graph.vertices[0]")
        for (i in graph.getMatrix()) {
            assertEquals(5, i.size, "Size matrix before add edges")
        }
        for (i in 0..4) {
            for (j in 0..4) {
                graph.addEdge(vertices[i], vertices[j], edges[i * 5 + j].toInt())
            }
        }
        for (i in graph.getMatrix()) {
            assertEquals(5, i.size, "Size matrix after add edges")
        }
        for (i in 0..4) {
            for (j in 0..4) {
                assertEquals(edges[i * 5 + j].toInt(), graph.getMatrix()[i][j])
            }
        }
    }

    @Test
    fun delEdge() {
        val string = "0 5 0 3 10 " +
                "0 0 4 0 0 " +
                "0 0 0 0 0 " +
                "0 4 1 0 0 " +
                "0 0 0 2 0"
        val edges = string.split(" ")
        val stringVer: String = "A B C D E"

        val vertices = stringVer.split(" ")

        for (i in vertices) {
            graph.addVertex(i)
        }
        for (i in 0..4) {
            for (j in 0..4) {
                graph.addEdge(vertices[i], vertices[j], edges[i * 5 + j].toInt())
            }
        }
        for (i in 0..4) {
            for (j in 0..4) {
                graph.delEdge(graph.getVertices()[i].toString(), graph.getVertices()[j].toString())
                assertEquals(
                    graph.getMatrix()[graph.getVertices()[i].getIndex()][graph.getVertices()[j].getIndex()],
                    0,
                    "Check delete weight(index[${i},${j}])"
                )
            }
        }
    }

    @Test
    fun delVertex() {
        val string = "0 5 0 3 10 " +
                "0 0 4 0 0 " +
                "0 0 0 0 0 " +
                "0 4 1 0 0 " +
                "0 0 0 2 0"
        val edges = string.split(" ")
        val stringVer: String = "A B C D E"

        val vertices = stringVer.split(" ")

        for (i in vertices) {
            graph.addVertex(i)
        }
        for (i in 0..4) {
            for (j in 0..4) {
                graph.addEdge(vertices[i], vertices[j], edges[i * 5 + j].toInt())
            }
        }
        var tempSize = 5
        assertEquals(tempSize, graph.getMatrix().size)

        for (i in vertices.shuffled()) {
            graph.delVertex(i)
            tempSize -= 1
            assertEquals(tempSize, graph.getMatrix().size)
        }
    }

    @Test
    fun testRandomGraph() {
        graph = Graph(GraphType.RandomGraph)
        assertEquals(graph.getVertices().size, graph.getMatrix().size)
    }
}