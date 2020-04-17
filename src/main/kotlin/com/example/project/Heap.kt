package com.example.project

import com.example.project.option.Option
import com.example.project.result.Result
import java.lang.IllegalStateException

sealed class Heap<A> {

    internal abstract val left: Result<Heap<A>>
    internal abstract val right: Result<Heap<A>>
    internal abstract val head: Result<A>
    internal abstract val comparator: Result<Comparator<A>>
    protected abstract val rank: Int
    abstract val size: Int
    abstract val isEmpty: Boolean

    override fun toString(): String {
        val strLeft = left.map { "$it" }.getOrElse("F")
        val strHead = head.map { "$it" }.getOrElse("F")
        val strRight = right.map { "$it" }.getOrElse("F")
        return "{$rank $strLeft $strHead $strRight $comparator}"
    }

    class Empty<A>(
            override val comparator: Result<Comparator<A>> =
                    Result.Empty as Result<Comparator<A>>) : Heap<A>() {

        override fun equals(other: Any?): Boolean = other is Empty<*>
        override fun hashCode(): Int = 41
        override fun toString(): String = "{E $comparator}"
        override val isEmpty: Boolean = true
        override val left: Result<Heap<A>> = Result.failure("left called on empty heap")
        override val right: Result<Heap<A>> = Result.failure("right called on empty heap")
        override val head: Result<A> = Result.failure("head called on empty heap")
        override val rank: Int = 0
        override val size: Int = 0

    }

    internal class H<A>(
            override val rank: Int,
            internal val lt: Heap<A>,
            internal val hd: A,
            internal val rg: Heap<A>,
            override val comparator: Result<Comparator<A>> =
                    lt.comparator.orElse { rg.comparator }) : Heap<A>() {

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

    }

    companion object {
        operator fun <A : Comparable<A>> invoke(): Heap<A> = Empty()

        operator fun <A : Comparable<A>> invoke(element: A): Heap<A> =
                H(1, Empty(), element, Empty())

        operator fun <A> invoke(comparator: Comparator<A>) = Empty(Result(comparator))

        operator fun <A> invoke(comparator: Result<Comparator<A>>) = Empty(comparator)

        operator fun <A> invoke(element: A, comparator: Comparator<A>): Heap<A> =
                H(1,
                        Empty(Result<Comparator<A>>(comparator)),
                        element,
                        Empty(Result<Comparator<A>>(comparator)),
                        Result(comparator))

        operator fun <A> invoke(element: A, comparator: Result<Comparator<A>>): Heap<A> =
                H(1,
                        Empty(comparator),
                        element,
                        Empty(comparator),
                        comparator)

        private fun <A> mergeHelper(
                left: Heap<A>,
                head: A,
                right: Heap<A>,
                comparator: Result<Comparator<A>> =
                        left.comparator.orElse { right.comparator }): Heap<A> = when {
            left.rank < right.rank -> H(left.rank + 1, right, head, left, comparator)
            else -> H(right.rank + 1, left, head, right, comparator)
        }

        fun <A : Comparable<A>> merge(first: Heap<A>, second: Heap<A>): Heap<A> = when (first) {
            is Empty -> second
            is H -> when (second) {
                is Empty -> first
                is H -> first.comparator
                        .orElse { second.comparator }
                        .orElse { Result.failure("no comparator found") }.let { comparator ->
                            when (comparator) {
                                is Result.Success -> {
                                    println("comparator is Success: $first, $second")
                                    val smaller = if (comparator.value.compare(first.hd, second.hd) < 0) first else second
                                    val greater = if (comparator.value.compare(first.hd, second.hd) < 0) second else first
                                    val right = merge(smaller.rg, greater)
                                    mergeHelper(smaller.lt, smaller.hd, right, comparator)
                                }
                                else -> {
                                    println("comparator is Failure: $first, $second")
                                    val smaller = if (first.hd < second.hd) first else second
                                    val greater = if (first.hd < second.hd) second else first
                                    val right = merge(smaller.rg, greater)
                                    mergeHelper(smaller.lt, smaller.hd, right)
                                }
                            }
                        }
            }
        }

        @JvmName("mergeComparator")
        fun <A> merge(first: Heap<A>, second: Heap<A>): Heap<A> = when (first) {
            is Empty -> second
            is H -> when (second) {
                is Empty -> first
                is H -> first.comparator
                        .orElse { second.comparator }
                        .getOrElse { throw IllegalStateException("no comparator found") }.let { comparator ->
                            val smaller = if (comparator.compare(first.hd, second.hd) < 0) first else second
                            val greater = if (comparator.compare(first.hd, second.hd) < 0) second else first
                            val right = merge(smaller.rg, greater)
                            mergeHelper(smaller.lt, smaller.hd, right, Result(comparator))
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

@JvmName("plusComparator")
operator fun <A> Heap<A>.plus(element: A): Heap<A> = Heap.merge(this, Heap(element, this.comparator))
operator fun <A: Comparable<A>> Heap<A>.plus(element: A): Heap<A> = Heap.merge(this, Heap(element, this.comparator))

@JvmName("tailComparator")
internal fun <A> Heap.H<A>.tail(): Result<Heap<A>> = when (this) {
    is Heap.H -> Result(Heap.merge(lt, rg))
    else -> Result.failure("tail called on empty heap")
}
internal fun <A: Comparable<A>> Heap.H<A>.tail(): Result<Heap<A>> = when (this) {
    is Heap.H -> Result(Heap.merge(lt, rg))
    else -> Result.failure("tail called on empty heap")
}
@JvmName("getComparator")
fun <A> Heap<A>.get(index: Int): Result<A> = when (this) {
    is Heap.H -> when (index) {
        0 -> head
        else -> this.tail().flatMap { it.get(index - 1) }
    }
    else -> Result.failure("element not found")
}
fun <A: Comparable<A>> Heap<A>.get(index: Int): Result<A> = when (this) {
    is Heap.H -> when (index) {
        0 -> head
        else -> this.tail().flatMap { it.get(index - 1) }
    }
    else -> Result.failure("element not found")
}
@JvmName("popComparator")
fun <A> Heap<A>.pop(): Option<Pair<A, Heap<A>>> = when (this) {
    is Heap.H -> Option.Some(Pair(hd, Heap.merge(lt, rg)))
    else -> Option.None as Option<Pair<A, Heap<A>>>
}
fun <A: Comparable<A>> Heap<A>.pop(): Option<Pair<A, Heap<A>>> = when (this) {
    is Heap.H -> Option.Some(Pair(hd, Heap.merge(lt, rg)))
    else -> Option.None as Option<Pair<A, Heap<A>>>
}
@JvmName("toListComparator")
fun <A> Heap<A>.toList(): LinkedList<A> = foldLeft(LinkedList<A>()) { list ->
    { element -> list.cons(element) }
}.reverse()
fun <A: Comparable<A>> Heap<A>.toList(): LinkedList<A> = foldLeft(LinkedList<A>()) { list ->
    { element -> list.cons(element) }
}.reverse()
@JvmName("foldLeftComparator")
fun <A, B> Heap<A>.foldLeft(identity: B, f: (B) -> (A) -> B): B =
        unfold(this, identity, { it.pop() }, f)
fun <A: Comparable<A>, B> Heap<A>.foldLeft(identity: B, f: (B) -> (A) -> B): B =
        unfold(this, identity, { it.pop() }, f)


