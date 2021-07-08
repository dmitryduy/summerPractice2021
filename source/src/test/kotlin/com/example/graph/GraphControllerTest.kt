package com.example.graph

import org.junit.jupiter.api.Assertions.*
import org.junit.runners.Parameterized

internal class GraphControllerTest {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun graphs(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 0 1\n" +
                            "3 2 1\n" +
                            "a b c"
                ),
                arrayOf(
                    "-3\n" +
                            "7 2 1\n" +
                            "0 0 1\n" +
                            "3 2 1\n" +
                            "a b c"
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 0 1\n" +
                            "3 2 1\n" +
                            "a b c\n" +
                            "d e f"
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 0 1 2\n" +
                            "3 2 1\n" +
                            "a b c\n"
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 -5 1\n" +
                            "3 2 1\n" +
                            "a b c"
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 5 1\n" +
                            "3 2 1\n" +
                            "a b"
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 5 1\n" +
                            "3 2 1\n" +
                            "a b b\n"
                )
            )
        }
    }
}