package com.example.project

sealed class List<A> {

    abstract fun isEmpty(): Boolean
    fun cons(a: A): List<A> = Cons(a, this)
    fun setHead(a: A): List<A> = when (this) {
        Nil -> throw IllegalArgumentException("setHead called on an empty list")
        is Cons -> Cons(a, this.tail)
    }

    fun drop(n: Int): List<A> {
        tailrec fun go(n: Int, decc: List<A>): List<A> = when (decc) {
            Nil -> Nil as List<A>
            is Cons -> when (n) {
                0 -> decc
                else -> go(n - 1, decc.tail)
            }
        }
        return go(n, this)
    }

    fun dropWhile(p: (A) -> Boolean): List<A> {
        tailrec fun go(p: (A) -> Boolean, decc: List<A>): List<A> = when (decc) {
            Nil -> Nil as List<A>
            is Cons -> if (p(decc.head)) go(p, decc.tail) else decc
        }
        return go(p, this)
    }

    private object Nil : List<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[NIL]"

    }

    private class Cons<A>(
            internal val head: A,
            internal val tail: List<A>
    ) : List<A>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}NIL]"

        private tailrec fun toString(acc: String, list: List<A>): String =
                when (list) {
                    is Nil -> acc
                    is Cons -> toString("$acc${list.head}, ", list.tail)
                }
    }

    companion object {

        operator
        fun <A> invoke(vararg az: A): List<A> =
                az.foldRight(Nil as List<A>) { a: A, list: List<A> ->
                    Cons(a, list)
                }

    }
}