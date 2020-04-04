package com.example.project

import com.example.project.result.Result
import com.example.project.result.map2

sealed class List<A> {

    abstract fun isEmpty(): Boolean
    fun cons(a: A): List<A> = Cons(a, this)
    fun setHead(a: A): List<A> = when (this) {
        Nil -> throw IllegalArgumentException("setHead called on an empty list")
        is Cons -> Cons(a, this.tail)
    }

    abstract val length: Int

    abstract fun headSafe(): Result<A>

    fun lastSafe(): Result<A> = reverse().headSafe()

    fun drop(n: Int): List<A> = drop(n, this)

    fun dropWhile(p: (A) -> Boolean): List<A> = dropWhile(p, this)

    fun <B> foldLeft(initial: B, f: (B, A) -> B): B = foldLeft(this, initial, f)

    fun <B> foldRight(initial: B, f: (A, B) -> B): B = when (this) {
        Nil -> initial
        is Cons -> {
            f(head, tail.foldRight(initial, f))
        }
    }

    fun <B> coFoldRight(initial: B, f: (A, B) -> B) = coFoldRight(this.reverse(), initial, initial, f)

    fun <B> foldLeft(identity: B, zero: B, f: (B, A) -> B): B {
        fun go(acc: B, list: List<A>): B = when (list) {
            Nil -> acc
            is Cons -> {
                if (acc == zero)
                    acc
                else
                    go(f(acc, list.head), list.tail)
            }
        }
        return go(identity, this)
    }

    fun length(): Int = length

    fun reverse(): List<A> = foldLeft(Nil as List<A>) { list, elem -> list.cons(elem) }

    fun init(): List<A> = when (this) {
        Nil -> Nil as List<A>
        is Cons -> this.reverse().drop(1).reverse()
    }

    fun concat(other: List<A>) = Companion.concat(this, other)

    fun <B> map(f: (A) -> B): List<B> = coFoldRight(Nil as List<B>) { elem, list -> list.cons(f(elem)) }

    fun <B> flatMap(f: (A) -> List<B>): List<B> = flatten(map(f))

    fun filter(p: (A) -> Boolean): List<A> = coFoldRight(Nil as List<A>) {
        elem, list -> if (p(elem)) list.cons(elem) else list }

    fun <A1, A2> unzip(f: (A) -> Pair<A1, A2>): Pair<List<A1>, List<A2>> =
            coFoldRight(Pair(List.Companion(), List.Companion())) { elem, pairOfLists ->
                f(elem).let {
                    Pair(pairOfLists.first.cons(it.first), pairOfLists.second.cons(it.second))
                }
            }

    fun getAt(n: Int): Result<A> = when {
        n < 0 || n >= length -> Result.failure(IndexOutOfBoundsException())
        else -> foldLeft(MyPair(Result<A>(), n+1), MyPair(Result<A>(), 0), { acc, elem ->
            MyPair(Result(elem), acc.second - 1)
        }).first
    }


    internal object Nil : List<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[NIL]"

        override fun equals(other: Any?): Boolean = other is Nil

        override fun hashCode() = 41

        override val length: Int = 0

        override fun headSafe(): Result<Nothing> = Result.failure("empty list")

    }

    internal data class Cons<A>(
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

        override val length: Int = 1 + tail.length

        override fun headSafe(): Result<A> = Result(head)

    }


    companion object {

        operator
        fun <A> invoke(vararg az: A): List<A> =
                az.foldRight(Nil as List<A>) { a: A, list: List<A> ->
                    Cons(a, list)
                }

        fun <A> flatten(list: List<List<A>>): List<A> = List.foldLeft(list, List.Nil as List<A>, { a, b -> a.concat(b) })

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
            is Cons -> {
                if (list.tail.isEmpty()) f(acc, list.head) else foldLeft(list.tail, f(acc, list.head), f)
            }
        }

        private tailrec fun <A, B> coFoldRight(list: List<A>, initial: B, acc: B, f: (A, B) -> B): B = when (list) {
            Nil -> acc
            is Cons -> {
                coFoldRight(list.tail, initial, f(list.head, acc), f)
            }
        }

        fun <A> concat(a: List<A>, b: List<A>): List<A> = foldLeft(a.reverse(), b, { list, elem -> list.cons(elem) })

    }
}

fun List<Double>.sum() = this.foldLeft(0.0) { a, b -> a + b }
fun List<Int>.sum() = this.foldLeft(0) { a, b -> a + b }
fun List<Int>.sumFoldRight() = this.foldRight(0) { a, b -> a + b }
fun List<Int>.sumCoFoldRight() = this.coFoldRight(0) { a, b -> a + b }
fun List<Int>.product() = this.foldLeft(1) { a, b -> a * b }

fun List<Int>.times3() = this.map { it * 3 }
fun List<Double>.doubleToString() = this.map { it.toString() }

fun <A> flattenResult(list: List<Result<A>>): List<A> = list.flatMap {
    ra -> ra.map { List(it) }.getOrElse(List())
}

fun <A> sequence2(list: List<Result<A>>): Result<List<A>> =
        list.filter{ it !is Result.Empty }.reverse().foldLeft(Result(List())) { l, ra ->
            map2(ra, l) { a -> { la: List<A> -> la.cons(a) }}
}

fun <A, B> traverse(list: List<A>, f: (A) -> Result<B>): Result<List<B>> = list.coFoldRight(Result(List<B>())) { a, l ->
    map2(f(a), l) { b -> { lb: List<B> -> lb.cons(b) }}
}

fun <A> sequence(list: List<Result<A>>): Result<List<A>> = traverse(list) { ra: Result<A> -> ra }

fun <A, B, C> zipWith(lista: List<A>, listb: List<B>, f: (A) -> (B) -> C): List<C> {
    tailrec fun go(list1: List<A>, list2: List<B>, acc: List<C>): List<C> = when (list1) {
        List.Nil -> acc
        is List.Cons -> when (list2) {
            List.Nil -> acc
            is List.Cons -> go(list1.tail, list2. tail, acc.cons(f(list1.head)(list2.head)))
        }
    }
    return go(lista, listb, List()).reverse()
}

fun <A, B, C> product(lista: List<A>, listb: List<B>, f: (A) -> (B) -> C) = lista.flatMap {
    a -> listb.map { b -> f(a)(b) }
}

fun <A, B> unzip(list: List<Pair<A, B>>): Pair<List<A>, List<B>> = list.unzip { it }

data class MyPair<B>(val first: Result<B>, val second: Int) {
    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other::class == this::class -> (other as MyPair<B>).second == second
        else -> false
    }

    override fun hashCode(): Int = first.hashCode() + 41*second.hashCode()
}