package com.example.dijkstra

import com.example.graph.Graph
import com.example.graph.Vertex
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class DijkstraTest {

    @Test
    fun make_algorithm() {
        val a = Dijkstra()
        a.makeAlgorithm(Graph(), Vertex("A", 2))
    }
}