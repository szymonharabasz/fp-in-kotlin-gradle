package com.example.project

import com.example.project.option.Option
import com.example.project.result.Result
import com.example.project.result.map2
import com.sun.jdi.connect.spi.TransportService
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import javax.swing.plaf.nimbus.NimbusLookAndFeel
import kotlin.IllegalStateException

typealias LL<A> = LinkedList<LinkedList<A>>

sealed class LinkedList<A> {

    abstract fun isEmpty(): Boolean
    fun cons(a: A): LinkedList<A> = Cons(a, this)
    fun setHead(a: A): LinkedList<A> = when (this) {
        Nil -> throw IllegalArgumentException("setHead called on an empty list")
        is Cons -> Cons(a, this.tail)
    }

    abstract val length: Int

    abstract fun headSafe(): Result<A>

    fun lastSafe(): Result<A> = reverse().headSafe()

    fun drop(n: Int): LinkedList<A> = drop(n, this)

    fun dropWhile(p: (A) -> Boolean): LinkedList<A> = dropWhile(p, this)

    fun <B> foldLeft(initial: B, f: (B, A) -> B): B = foldLeft(this, initial, f)

    fun <B> foldRight(initial: B, f: (A, B) -> B): B = when (this) {
        Nil -> initial
        is Cons -> {
            f(head, tail.foldRight(initial, f))
        }
    }

    fun <B> coFoldRight(initial: B, f: (A, B) -> B) = coFoldRight(this.reverse(), initial, initial, f)

    fun <B> coFoldRight(initial: B, zero: B, f: (A, B) -> B) = coFoldRight(this.reverse(), initial, zero, initial, f)

    fun <B> foldLeft(identity: B, zero: B, f: (B, A) -> B): Pair<B, LinkedList<A>> {
        fun go(acc: B, list: LinkedList<A>): Pair<B, LinkedList<A>> = when (list) {
            Nil -> Pair(acc, list)
            is Cons -> {
                if (acc == zero)
                    Pair(acc, list)
                else
                    go(f(acc, list.head), list.tail)
            }
        }
        return go(identity, this)
    }

    fun <B> parFoldLeft(
            es: ExecutorService,
            identity: B,
            f: (B, A) -> B,
            m: (B, B) -> B
    ): Result<B> = try {
            val result: LinkedList<B> = divide(10).map { list: LinkedList<A> ->
                es.submit<B> { list.foldLeft(identity, f) }
            }.map { fb ->
                try {
                    fb.get()
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                } catch (e: ExecutionException) {
                    throw RuntimeException(e)
                }
            }
            Result(result.foldLeft(identity, m))
        } catch (e: Exception) {
            Result.failure<B>(e)
        }

    fun <B> parMap(es: ExecutorService, g: (A) -> B): Result<LinkedList<B>> = try {
        val result: LinkedList<B> = this.map { es.submit<B> { g(it) } }.map { fb ->
            try {
                fb.get()
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            } catch (e: ExecutionException) {
                throw RuntimeException(e)
            }
        }
        Result(result)

    } catch (e: Exception) {
        Result.failure<LinkedList<B>>(e)
    }

    fun exists(p: (A) -> Boolean): Boolean = foldLeft(false, true) {
        _, a -> p(a)
    }.first

    fun forAll(p: (A) -> Boolean): Boolean = !exists { !p(it) }

    fun length(): Int = length

    fun reverse(): LinkedList<A> = foldLeft(Nil as LinkedList<A>) { list, elem -> list.cons(elem) }

    fun init(): LinkedList<A> = when (this) {
        Nil -> Nil as LinkedList<A>
        is Cons -> this.reverse().drop(1).reverse()
    }

    fun concat(other: LinkedList<A>) = Companion.concat(this, other)

    fun <B> map(f: (A) -> B): LinkedList<B> = coFoldRight(Nil as LinkedList<B>) { elem, list -> list.cons(f(elem)) }

    fun <B> flatMap(f: (A) -> LinkedList<B>): LinkedList<B> = flatten(map(f))

    fun filter(p: (A) -> Boolean): LinkedList<A> = coFoldRight(Nil as LinkedList<A>) {
        elem, list -> if (p(elem)) list.cons(elem) else list }

    fun <A1, A2> unzip(f: (A) -> Pair<A1, A2>): Pair<LinkedList<A1>, LinkedList<A2>> =
            coFoldRight(Pair(Companion(), Companion())) { elem, pairOfLists ->
                f(elem).let {
                    Pair(pairOfLists.first.cons(it.first), pairOfLists.second.cons(it.second))
                }
            }

    fun getAt(n: Int): Result<A> = when {
        n < 0 || n >= length -> Result.failure(IndexOutOfBoundsException())
        else -> foldLeft(MyPair(Result<A>(), n+1), MyPair(Result<A>(), 0), { acc, elem ->
            MyPair(Result(elem), acc.second - 1)
        }).first.first
    }

    fun splitAt(n: Int): Pair<LinkedList<A>, LinkedList<A>> = when {
        n < 0 -> Pair(Companion<A>(), this)
        else -> foldLeft(MyPair(Pair(Companion<A>(),this), n), MyPair(Pair(Companion<A>(),this), 0), { acc, elem ->
            MyPair(Pair(acc.first.first.cons(elem), acc.first.second.drop(1)), acc.second - 1)
        }).first.first.let { Pair(it.first.reverse(), it.second) }
    }

    private fun splitListAt(n: Int): LinkedList<LinkedList<A>> = splitAt(n).let { LinkedList(it.first, it.second) }

    fun divide(depth: Int): LinkedList<LinkedList<A>> {
        tailrec fun go(depths: LinkedList<Int>, acc: LL<A>, decc: LL<A>): LL<A> = when (depths) {
            is Cons -> when (decc) {
                is Cons -> {
                    val d = depths.head
                    val n = decc.head.length / 2
                    val split = decc.head.splitListAt(n)
                    if (split is Cons && split.tail is Cons) {
                        if (d == 1) {
                            val nextAcc = when {
                                split.head == Nil && split.tail.head == Nil -> acc
                                split.head == Nil -> acc.cons(split.tail.head)
                                split.tail.head == Nil -> acc.cons(split.head)
                                else -> acc.cons(split.head).cons(split.tail.head)
                            }
                            go (depths.tail, nextAcc, decc.tail)
                        } else {
                            go(
                                    depths.tail.cons(d - 1).cons(d - 1),
                                    acc,
                                    decc.tail.cons(split.tail.head).cons(split.head)
                            )
                        }
                    } else {
                        throw IllegalStateException("should never happen")
                    }
                }
                else -> throw IllegalStateException("should never happen")
            }
            else -> acc
        }
        return go(LinkedList(depth), LinkedList.Companion(), LinkedList(this)).reverse()
    }

    fun startsWith(sub: LinkedList<A>): Boolean {
        tailrec fun go(list: LinkedList<A>, sublist: LinkedList<A>, acc: Boolean): Boolean = when {
            !acc -> acc
            else -> when (list) {
                Nil -> when (sublist) {
                    Nil -> true
                    else -> false
                }
                is Cons -> when (sublist) {
                    Nil -> acc
                    is Cons -> go(list.tail, sublist.tail, list.head == sublist.head)
                }
            }
        }
        return go(this, sub, true)
    }

    fun hasSublist(sub: LinkedList<A>): Boolean {
        tailrec fun go(list: LinkedList<A>): Boolean = when {
            list.startsWith(sub) -> true
            list is Cons -> go(list.tail)
            else -> false
        }
        return go(this)
    }

    fun <B> groupBy(f: (A) -> B): MyLinkedMap<B, LinkedList<A>> = coFoldRight(MyLinkedMap<B, LinkedList<A>>()) { a, map ->
        f(a).let {
            map.set(it, map.get(it).getOrElse(LinkedList.Companion<A>()).cons(a))
        }
    }

    internal object Nil : LinkedList<Nothing>() {

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[NIL]"

        override fun equals(other: Any?): Boolean = other is Nil

        override fun hashCode() = 41

        override val length: Int = 0

        override fun headSafe(): Result<Nothing> = Result.failure("empty list")

    }

    internal data class Cons<A>(
            internal val head: A,
            internal val tail: LinkedList<A>
    ) : LinkedList<A>() {

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}NIL]"

        private tailrec fun toString(acc: String, list: LinkedList<A>): String =
                when (list) {
                    is Nil -> acc
                    is Cons -> toString("$acc${list.head}, ", list.tail)
                }

        override val length: Int = 1 + tail.length

        override fun headSafe(): Result<A> = Result(head)

    }


    companion object {

        operator
        fun <A> invoke(vararg az: A): LinkedList<A> =
                az.foldRight(Nil as LinkedList<A>) { a: A, list: LinkedList<A> ->
                    Cons(a, list)
                }

        fun <A> flatten(list: LinkedList<LinkedList<A>>): LinkedList<A> = LinkedList.foldLeft(list, LinkedList.Nil as LinkedList<A>, { a, b -> a.concat(b) })

        tailrec fun <A> drop(n: Int, decc: LinkedList<A>): LinkedList<A> = when (decc) {
            Nil -> Nil as LinkedList<A>
            is Cons -> when (n) {
                0 -> decc
                else -> drop(n - 1, decc.tail)
            }
        }

        tailrec fun <A> dropWhile(p: (A) -> Boolean, decc: LinkedList<A>): LinkedList<A> = when (decc) {
            Nil -> Nil as LinkedList<A>
            is Cons -> if (p(decc.head)) dropWhile(p, decc.tail) else decc
        }

        tailrec fun <A, B> foldLeft(list: LinkedList<A>, acc: B, f: (B, A) -> B): B = when (list) {
            Nil -> acc
            is Cons -> {
                if (list.tail.isEmpty()) f(acc, list.head) else foldLeft(list.tail, f(acc, list.head), f)
            }
        }

        private tailrec fun <A, B> coFoldRight(list: LinkedList<A>, initial: B, acc: B, f: (A, B) -> B): B = when (list) {
            Nil -> acc
            is Cons -> {
                coFoldRight(list.tail, initial, f(list.head, acc), f)
            }
        }

        private tailrec fun <A, B> coFoldRight(
                list: LinkedList<A>,
                initial: B,
                zero: B,
                acc: B,
                f: (A, B) -> B): Pair<B, LinkedList<A>> =
                when (list) {
                    Nil -> Pair(acc, list)
                    is Cons -> {
                        println("acc = $acc, zero = $zero")
                        if (acc == zero) {
                            Pair(acc, list)
                        } else {0
                            coFoldRight(list.tail, initial, zero, f(list.head, acc), f)
                        }
                    }
                }

        /*


    fun <B> foldLeft(identity: B, zero: B, f: (B, A) -> B): Pair<B, LinkedList<A>> {
        fun go(acc: B, list: LinkedList<A>): Pair<B, LinkedList<A>> = when (list) {
            Nil -> Pair(acc, list)
            is Cons -> {
                if (acc == zero)
                    Pair(acc, list)
                else
                    go(f(acc, list.head), list.tail)
            }
        }
        return go(identity, this)
    }

         */
        fun <A> concat(a: LinkedList<A>, b: LinkedList<A>): LinkedList<A> = foldLeft(a.reverse(), b, { list, elem -> list.cons(elem) })

    }
}

fun LinkedList<Double>.sum() = this.foldLeft(0.0) { a, b -> a + b }
fun LinkedList<Int>.sum() = this.foldLeft(0) { a, b -> a + b }
fun LinkedList<Int>.sumFoldRight() = this.foldRight(0) { a, b -> a + b }
fun LinkedList<Int>.sumCoFoldRight() = this.coFoldRight(0) { a, b -> a + b }
fun LinkedList<Int>.product() = this.foldLeft(1) { a, b -> a * b }

fun LinkedList<Int>.times3() = this.map { it * 3 }
fun LinkedList<Double>.doubleToString() = this.map { it.toString() }

fun <A> flattenResult(list: LinkedList<Result<A>>): LinkedList<A> = list.flatMap {
    ra -> ra.map { LinkedList(it) }.getOrElse(LinkedList())
}

fun <A> sequence2(list: LinkedList<Result<A>>): Result<LinkedList<A>> =
        list.filter{ it !is Result.Empty }.reverse().foldLeft(Result(LinkedList())) { l, ra ->
            map2(ra, l) { a -> { la: LinkedList<A> -> la.cons(a) }}
}
/*
fun <A, B> traverse(list: LinkedList<A>, f: (A) -> Result<B>): Result<LinkedList<B>> = list.coFoldRight(Result(LinkedList<B>())) { a, l ->
    map2(f(a), l) { b -> { lb: LinkedList<B> -> lb.cons(b) }}
}
*/

fun <A, B> traverse(list: LinkedList<A>, f: (A) -> Result<B>): Result<LinkedList<B>> =
        list.reverse().coFoldRight(Result(LinkedList<B>()), Result()) { a, l ->
            map2(f(a), l) { b -> { lb: LinkedList<B> -> lb.cons(b) } }
        }.first

fun <A> sequence(list: LinkedList<Result<A>>): Result<LinkedList<A>> = traverse(list) { ra: Result<A> -> ra }

fun <A, B, C> zipWith(lista: LinkedList<A>, listb: LinkedList<B>, f: (A) -> (B) -> C): LinkedList<C> {
    tailrec fun go(list1: LinkedList<A>, list2: LinkedList<B>, acc: LinkedList<C>): LinkedList<C> = when (list1) {
        LinkedList.Nil -> acc
        is LinkedList.Cons -> when (list2) {
            LinkedList.Nil -> acc
            is LinkedList.Cons -> go(list1.tail, list2. tail, acc.cons(f(list1.head)(list2.head)))
        }
    }
    return go(lista, listb, LinkedList()).reverse()
}

fun <A, B, C> product(lista: LinkedList<A>, listb: LinkedList<B>, f: (A) -> (B) -> C) = lista.flatMap {
    a -> listb.map { b -> f(a)(b) }
}

fun <A, B> unzip(list: LinkedList<Pair<A, B>>): Pair<LinkedList<A>, LinkedList<B>> = list.unzip { it }

fun <A, S> unfold (z: S, f: (S) -> Option<Pair<A, S>>): LinkedList<A> {
    fun go(s: S, acc: LinkedList<A>): LinkedList<A> {
        return when (val fs = f(s)) {
            is Option.Some -> go(fs.value.second, acc.cons(fs.value.first))
            else -> acc
        }
    }
    return go(z, LinkedList<A>()).reverse()
}

fun myRange(start: Int, end: Int): LinkedList<Int> = unfold(start) { i ->
    if (i < end)
        Option(Pair(i, i + 1))
    else
        Option()
}

data class MyPair<B>(val first: B, val second: Int) {
    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other::class == this::class -> (other as MyPair<B>).second == second
        else -> false
    }

    override fun hashCode(): Int = first.hashCode() + 41*second.hashCode()
}

class MyLinkedMap<K, V> {
    private val list: LinkedList<Pair<K, V>>
    constructor() {
        this.list = LinkedList.Companion()
    }

    private constructor(l: LinkedList<Pair<K, V>>) {
        this.list = l
    }

    fun hasKey(key: K): Boolean = list.exists { it.first == key }

    fun get(key: K): Result<V> = list.dropWhile { it.first != key }.headSafe().map { it.second }

    fun set(key: K, value: V): MyLinkedMap<K, V> = if (hasKey(key)) {
        MyLinkedMap(list.foldLeft(LinkedList<Pair<K, V>>()) {
            acc, elem -> if (elem.first == key) acc.cons(Pair(key, value))
            else acc.cons(elem)
        })
    } else {
        MyLinkedMap(list.cons(Pair(key, value)))
    }

    fun keys(): LinkedList<K> = list.map { it.first }

    fun values(): LinkedList<V> = list.map { it.second }
}