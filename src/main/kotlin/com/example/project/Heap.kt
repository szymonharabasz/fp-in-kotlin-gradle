package com.example.project

import com.example.project.result.Result

sealed class Heap<A: Comparable<A>> {

    internal abstract val left: Result<Heap<A>>
    internal abstract val right: Result<Heap<A>>
    internal abstract val head: Result<A>
    protected abstract val rank: Int
    abstract val size: Int
    abstract val isEmpty: Boolean

    abstract class Empty<A: Comparable<A>>: Heap<A>() {

        override val isEmpty: Boolean = true
        override val left: Result<Heap<A>> = Result(E as Heap<A>)
        override val right: Result<Heap<A>> = Result(E as Heap<A>)
        override val head: Result<A> = Result.failure("head called on empty heap")
        override val rank: Int = 0
        override val size: Int = 0

    }

    internal object E: Empty<Nothing>()

    internal class H<A: Comparable<A>> (
            override val rank: Int,
            private val lt: Heap<A>,
            private val hd: A,
            private val rg: Heap<A>): Heap<A>() {

        override val left: Result<Heap<A>> = Result(lt)
        override val right: Result<Heap<A>> = Result(rg)
        override val head: Result<A> = Result(hd)
        override val size: Int = lt.size + rg.size + 1
        override val isEmpty: Boolean = false

    }

    companion object {
        operator fun <A: Comparable<A>> invoke(): Heap<A> = E as Heap<A>
    }

}