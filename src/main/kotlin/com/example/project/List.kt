package com.example.project

sealed class List<A> {

    abstract fun isEmpty(): Boolean
    fun cons(a: A): List<A> = Cons(a, this)
    fun setHead(a: A): List<A> = when (this) {
        Nil -> throw IllegalArgumentException("setHead called on an empty list")
        is Cons -> Cons(a, this.tail)
    }

    fun drop(n: Int): List<A> = drop(n, this)

    fun dropWhile(p: (A) -> Boolean): List<A> = dropWhile(p, this)

    fun <B> foldLeft(initial: B, f: (B, A) -> B): B = foldLeft(this, initial, f)

    fun <B> foldRight(initial: B, f: (A, B) -> B): B = when (this) {
        Nil -> initial
        is Cons -> f(head, tail.foldRight(initial, f))
    }

    fun length(): Int = foldLeft(0) { n, _ -> n + 1 }

    fun reverse(): List<A> = foldLeft(Nil as List<A>) { list, elem -> list.cons(elem) }

    fun init(): List<A> = when (this) {
        Nil -> Nil as List<A>
        is Cons -> this.reverse().drop(1).reverse()
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

        tailrec fun <A> drop(n: Int, decc: List<A>): List<A> = when (decc) {
            Nil -> Nil as List<A>
            is Cons -> when (n) {
                0 -> decc
                else -> drop(n - 1, decc.tail)
            }
        }

        tailrec fun <A> dropWhile(p: (A) -> Boolean, decc: List<A>): List<A> = when (decc) {
            Nil -> Nil as List<A>
            is Cons -> if (p(decc.head)) dropWhile(p, decc.tail) else decc
        }

        tailrec fun <A, B> foldLeft(list: List<A>, acc: B, f: (B, A) -> B): B = when (list) {
            Nil -> acc
            is Cons -> if (list.tail.isEmpty()) f(acc, list.head) else foldLeft(list.tail, f(acc, list.head), f)
        }

    }
}

fun List<Int>.sum() = this.foldLeft(0) { a, b -> a + b }
fun List<Int>.product() = this.foldLeft(1) { a, b -> a * b }
