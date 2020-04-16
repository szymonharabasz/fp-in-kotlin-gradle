package com.example.project

import com.example.project.option.Option
import com.example.project.result.Result

sealed class Heap<A: Comparable<A>> {

    internal abstract val left: Result<Heap<A>>
    internal abstract val right: Result<Heap<A>>
    internal abstract val head: Result<A>
    abstract fun tail(): Result<Heap<A>>
    protected abstract val rank: Int
    abstract val size: Int
    abstract val isEmpty: Boolean
    abstract fun get(index: Int): Result<A>
    abstract fun pop(): Option<Pair<A, Heap<A>>>
    fun toList(): LinkedList<A> = foldLeft(LinkedList<A>()) { list ->
        { element -> list.cons(element) }
    }.reverse()
    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B): B =
            unfold(this, identity, { it.pop() }, f)

    operator fun plus(element: A): Heap<A> = merge(this, Heap(element))

    open class Empty<A : Comparable<A>> : Heap<A>() {

        override fun equals(other: Any?): Boolean = other is Empty<*>
        override fun hashCode(): Int = 41
        override val isEmpty: Boolean = true
        override val left: Result<Heap<A>> = Result.failure("left called on empty heap")
        override val right: Result<Heap<A>> = Result.failure("right called on empty heap")
        override val head: Result<A> = Result.failure("head called on empty heap")
        override val rank: Int = 0
        override val size: Int = 0
        override fun tail(): Result<Heap<A>> = Result.failure("tail called on empty heap")
        override fun get(index: Int): Result<A> = Result.failure("element not found")
        override fun pop(): Option<Pair<A, Heap<A>>> = Option.None as Option<Pair<A, Heap<A>>>

    }

    internal object E : Empty<Nothing>()

    internal class H<A : Comparable<A>>(
            override val rank: Int,
            internal val lt: Heap<A>,
            internal val hd: A,
            internal val rg: Heap<A>) : Heap<A>() {

        override fun equals(other: Any?): Boolean = when (other) {
            is H<*> -> hd == other.hd && lt == other.lt && rg == other.rg
            else -> false
        }

        override fun hashCode(): Int = hd.hashCode() + 41 * lt.hashCode() + 43 * rg.hashCode()
        override val left: Result<Heap<A>> = Result(lt)
        override val right: Result<Heap<A>> = Result(rg)
        override val head: Result<A> = Result(hd)
        override val size: Int = lt.size + rg.size + 1
        override val isEmpty: Boolean = false
        override fun tail(): Result<Heap<A>> = Result(merge(lt, rg))
        override fun get(index: Int): Result<A> = when (index) {
            0 -> head
            else -> tail().flatMap { it.get(index - 1) }
        }

        override fun pop(): Option<Pair<A, Heap<A>>> =
                Option.Some(Pair(hd, merge(lt, rg)))

    }

    companion object {
        operator fun <A : Comparable<A>> invoke(): Heap<A> = E as Heap<A>

        operator fun <A : Comparable<A>> invoke(element: A): Heap<A> =
                H(1,
                        Empty<A>(),
                        element,
                        Empty<A>())

        fun <A : Comparable<A>> merge(first: Heap<A>, second: Heap<A>): Heap<A> = when (first) {
            is Empty -> second
            is H -> when (second) {
                is Empty -> first
                is H -> {
                    val smaller = if (first.hd < second.hd) first else second
                    val greater = if (first.hd < second.hd) second else first
                    val right = merge(smaller.rg, greater)
                    H(right.rank + 1, smaller.lt, smaller.hd, right)
                }
            }
        }

    }
}

fun <A, B, S> unfold (z: S, identity: B, f: (S) -> Option<Pair<A, S>>, g: (B) -> (A) -> B): B {
    fun go(s: S, acc: B): B {
        return when (val fs = f(s)) {
            is Option.Some -> go(fs.value.second, g(acc)(fs.value.first))
            else -> acc
        }
    }
    return go(z, identity)
}