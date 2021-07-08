package com.example.graph

import org.junit.jupiter.api.Assertions.*
import org.junit.runners.Parameterized

internal class GraphControllerTest {

    fun test(string: String, boolean: Boolean) {

    }

    //имена объектов
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun graphs(): Collection<Array<Any?>> {
            return listOf(
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 0 1\n" +
                            "3 2 1\n" +
                            "a b c", true
                ),
                arrayOf(
                    "-3\n" +
                            "7 2 1\n" +
                            "0 0 1\n" +
                            "3 2 1\n" +
                            "a b c", false
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 0 1\n" +
                            "3 2 1\n" +
                            "a b c\n" +
                            "d e f", false
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 0 1 2\n" +
                            "3 2 1\n" +
                            "a b c\n", false
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 -5 1\n" +
                            "3 2 1\n" +
                            "a b c", false
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 5 1\n" +
                            "3 2 1\n" +
                            "a b", false
                ),
                arrayOf(
                    "3\n" +
                            "7 2 1\n" +
                            "0 5 1\n" +
                            "3 2 1\n" +
                            "a b b\n", false
                )
            )
        }
    }
}