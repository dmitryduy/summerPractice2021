package com.example.graph

import com.example.dijkstra.Dijkstra
import com.example.dijkstra.DijkstraState
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.runners.Parameterized
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame

class DijkstraStepsTest {
    @DisplayName("DijkstraStepsTest")
    @ParameterizedTest
    @MethodSource("countOfTests")
    fun test(number: Int) {
        val gr = Graph(GraphType.RandomGraph)
        val alg = Dijkstra()
        val steps = alg.makeAlgorithm(gr, gr.getVertices()[0])
        for (i in 0..steps.dijkstraSteps.size - 2) {
            val prev = steps.dijkstraSteps[i]
            val next = steps.dijkstraSteps[i + 1]
            var boolean = false
            when (next.getState()) {
                DijkstraState.VertexProcessing -> {
                    if (boolean) {
                        assertNotEquals(prev.getCurrVertex().toString(), next.getCurrVertex().toString())
                    } else {
                        boolean = true
                    }
                    assertEquals(prev.getQueue(), next.getQueue())
                    assertEquals(prev.getTable(), next.getTable())
                }
                DijkstraState.UpdatedQueue -> {
                    assertNotEquals(prev.getQueue(), next.getQueue())
                    assertEquals(prev.getCurrVertex(), next.getCurrVertex())
                    assertEquals(prev.getTable(), next.getTable())
                }
                DijkstraState.UpdatedPath -> {
                    assertEquals(prev.getQueue(), next.getQueue())
                    assertEquals(prev.getCurrVertex(), next.getCurrVertex())
                    assertEquals(prev.getTable(), next.getTable())
                }
                DijkstraState.UpdatedTable -> {
                    assertEquals(prev.getCurrVertex(), next.getCurrVertex())
                    assertEquals(prev.getQueue(), next.getQueue())
                    assertNotEquals(prev.getTable(), next.getTable())
                }
                else -> {
                    assertEquals(1, 2, "State start dont have index 0")
                }
            }
        }
    }

    @DisplayName("DijkstraStepsTest2")
    @ParameterizedTest
    @MethodSource("countOfTests")
    fun test2(number: Int) {
        val gr = Graph(GraphType.RandomGraph)
        val alg = Dijkstra()
        val steps = alg.makeAlgorithm(gr, gr.getVertices()[0])
        for (i in 0..steps.dijkstraSteps.size - 1) {
            val prev = steps.dijkstraSteps[i]
            if (prev.getState() != DijkstraState.VertexProcessing) continue
            var k = 0
            for (j in prev.getTable()[prev.getTable().size - 1]) {
                if (j.first == null && j.second != Integer.MAX_VALUE) {
                    k += 1
                }
            }
            assertEquals(gr.getVertices().size - prev.getQueue().size, k)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun countOfTests(): Collection<Array<Any>> {
            return listOf(
                arrayOf(1),
                arrayOf(2),
                arrayOf(3),
                arrayOf(4),
                arrayOf(5),
                arrayOf(1),
                arrayOf(2),
                arrayOf(3),
                arrayOf(4),
                arrayOf(5),
                arrayOf(1),
                arrayOf(2),
                arrayOf(3),
                arrayOf(4),
                arrayOf(5),
                arrayOf(1),
                arrayOf(2),
                arrayOf(3),
                arrayOf(4),
                arrayOf(5)
            )
        }
    }

}