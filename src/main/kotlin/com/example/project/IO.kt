package com.example.project

import com.example.project.option.Option

class IO<A>(private val f: () -> A) {
    operator fun invoke() = f()
    operator fun plus(io: IO<A>): IO<A> = IO {
        f()
        io.f()
    }
    fun <B> map(g: (A) -> B): IO<B> = IO { g(this()) }
    fun <B> flatMap(g: (A) -> IO<B>): IO<B> = IO { g(this())() }

    companion object {
        val empty: IO<Unit> = IO {}
        operator fun <A> invoke(a: A): IO<A> = IO { a }
        fun <A> repeat(n: Int, io: IO<A>): IO<LinkedList<A>> = IO {
            myRange(0, n).map { io() }
        }

        fun <A> doWhile(f: (A) -> Boolean, io: IO<A>) = IO {
            unfold(true) {
                if (it) io().let { ioa -> Option(Pair(ioa, f(ioa))) } else Option()
            }
        }

    }
}