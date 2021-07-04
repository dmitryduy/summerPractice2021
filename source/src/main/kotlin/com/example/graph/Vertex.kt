package com.example.graph

class Vertex(private var value: String, private var index: Int) {
    @JvmName("getValue1")
    fun getValue(): String {
        return value
    }

    @JvmName("getIndex1")
    fun getIndex(): Int {
        return index
    }

    fun setIndex(ind: Int) {
        index = ind
    }

    override fun toString(): String {
        return value
    }

}