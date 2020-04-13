package com.example.project.blackredtree


sealed class Color {

    abstract val blacker: Color
    abstract val redder: Color

    internal object R: Color() {
        override val blacker = B
        override val redder = NB

        override fun toString(): String = "R"
    }
    internal object B: Color() {
        override val blacker = BB
        override val redder = R

        override fun toString(): String = "B"
    }
    internal object BB: Color() {
        override val blacker: Color by lazy {
            throw IllegalStateException("can't make DoubleBlack blacker")
        }
        override val redder = B

        override fun toString(): String = "BB"
    }
    internal object NB: Color() {
        override val blacker = R
        override val redder: Color by lazy {
            throw IllegalStateException("can't make NegativeBlack redder")
        }

        override fun toString(): String = "NB"
    }
}