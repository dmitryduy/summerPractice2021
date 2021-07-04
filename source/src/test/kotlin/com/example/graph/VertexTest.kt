package com.example.graph

import org.junit.jupiter.api.Assertions.*

internal class VertexTest {
    val vertex = Vertex("A", 0)

    @org.junit.jupiter.api.Test
    fun getValue1() {
        assertEquals("A", vertex.getValue())
    }

    @org.junit.jupiter.api.Test
    fun getIndex1() {
        assertEquals(0, vertex.getIndex())
        vertex.setIndex(1)
        assertEquals(1, vertex.getIndex())
    }

    @org.junit.jupiter.api.Test
    fun testToString() {
        assertEquals("A", vertex.toString())
    }
}