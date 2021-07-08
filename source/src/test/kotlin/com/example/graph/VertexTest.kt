package com.example.graph

import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.omg.CORBA.Object
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.measureNanoTime

internal class VertexTest() {
    @DisplayName("Test with @MethodSource")
    @ParameterizedTest
    @MethodSource("vertex")
    fun test(name: String, index: Int) {
        val vertex = Vertex(name, index)
        assertEquals(vertex.getValue(), name)
        assertEquals(vertex.getIndex(), index)
        vertex.setIndex(3)
        assertEquals(vertex.getIndex(), 3)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun vertex(): Collection<Array<Any>> {
            return listOf(
                arrayOf("A", 1),         // First test:  (paramOne = 1, paramTwo = "I")
                arrayOf("B", 2), // Second test: (paramOne = 1999, paramTwo = "MCMXCIX")
                arrayOf("qwerty", Integer.MIN_VALUE) // Second test: (paramOne = 1999, paramTwo = "MCMXCIX")
            )
        }
    }
}
