package com.example.project

import com.example.project.result.Result

sealed class Stream<A> {

    abstract fun isEmpty(): Boolean

    abstract fun head(): Result<A>

    abstract fun tail(): Result<Stream<A>>

    fun takeAtMost(n: Int): Stream<A> = when (this) {
        Empty -> this
        is Cons -> if (n == 0) Empty as Stream<A> else Cons(hd, Lazy{ tl().takeAtMost(n - 1)} )
    }

    fun dropAtMost(n: Int): Stream<A> {
        tailrec fun go(m: Int, decc: Stream<A>): Stream<A> = when (decc) {
            Empty -> decc
            is Cons -> if (m == 0) decc else go(m - 1, decc.tl())
        }
        return go(n, this)
    }

    fun takeWhile(p: (A) -> Boolean): Stream<A> = when (this) {
        Empty -> this
        is Cons -> if (p(hd())) Cons(hd, Lazy{ tl().takeWhile(p) }) else Empty as Stream<A>
    }

    fun dropWhile(p: (A) -> Boolean): Stream<A> {
        tailrec fun go(decc: Stream<A>): Stream<A> = when (decc) {
            Empty -> decc
            is Cons -> if (p(decc.hd())) go(decc.tl()) else decc
        }
        return go (this)
    }

    fun toList(): LinkedList<A> = Companion.toList(this, LinkedList()).reverse()

    fun exists(p: (A) -> Boolean): Boolean {
        tailrec fun go(stream: Stream<A>): Boolean = when (stream) {
            Empty -> false
            is Cons -> if (p(stream.hd())) true else go(stream.tl())
        }
        return go(this)
    }

    fun <B> foldRight(z: Lazy<B>, f: (A) -> (Lazy<B>) -> B): B = when (this) {
        Empty -> z()
        is Cons -> f(hd())(Lazy { tl().foldRight(z, f) })
    }

    private object Empty: Stream<Nothing>() {

        override fun isEmpty() = true

        override fun head(): Result<Nothing> = Result()

        override fun tail(): Result<Stream<Nothing>> = Result()

    }

    private class Cons<A> (internal val hd: Lazy<A>, internal val tl: Lazy<Stream<A>>): Stream<A>() {

        override fun isEmpty(): Boolean = false

        override fun head(): Result<A>  = Result(hd())

        override fun tail(): Result<Stream<A>> = Result(tl())

    }

    companion object {

        fun <A> cons(hd: Lazy<A>, tl: Lazy<Stream<A>>): Stream<A> = Cons(hd, tl)

        operator fun <A> invoke(): Stream<A> = Empty as Stream<A>

        fun from(i: Int): Stream<Int> = iterate(i) { it + 1 }

        fun <A> repeat(f: () -> A): Stream<A> = cons(Lazy{ f() }, Lazy{ repeat(f) } )

        tailrec fun <A> toList(stream: Stream<A>, acc: LinkedList<A>): LinkedList<A> = when (stream) {
            Empty -> acc
            is Cons -> toList(stream.tl(), acc.cons(stream.hd()))
        }

        fun <A> iterate(seed: A, f: (A) -> A): Stream<A> = cons(Lazy{ seed }, Lazy{ iterate(f(seed), f) })
    }
}